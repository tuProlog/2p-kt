"""Throwaway (#1017): converts plain kotlin.test classes into Kotest FunSpecs.

Usage: plain2spec.py [--dry] FILE...   Each `class TestFoo`/`FooTest` holding only @Test/@Ignore methods becomes
`class FooSpec : FunSpec({ test("method-name") { ... } })` in a sibling file; other classes are reported and left
alone. Prints old->new mappings as JSON lines (module-relative) for parity.csv.
"""
import json, re, sys
from pathlib import Path

dry = "--dry" in sys.argv


def kebab(s):
    s = re.sub(r"([A-Z]+)([A-Z][a-z])", r"\1-\2", s)
    s = re.sub(r"([a-z0-9])([A-Z])", r"\1-\2", s)
    s = re.sub(r"([a-zA-Z])([0-9])", r"\1\2", s)
    return s.replace("_", "-").lower()


def leaf_name(meth):
    m = meth[4:] if meth.startswith("test") and meth[4:5].isupper() else meth
    return kebab(m[0].lower() + m[1:]) if m else kebab(meth)


def block_end(text, i):
    depth = 1
    while depth:
        c = text[i]
        if c == '"':  # skip strings (incl. templates) naively
            if text.startswith('"""', i):
                i = text.index('"""', i + 3) + 3
                continue
            j = i + 1
            while text[j] != '"':
                j += 2 if text[j] == "\\" else 1
            i = j + 1
            continue
        depth += (c == "{") - (c == "}")
        i += 1
    return i


def companion_members(decl):
    """The companion's members as private top-level declarations (they keep their meaning: it holds no state)."""
    body = decl.split("\n")[1:]
    body = body[: max(i for i, l in enumerate(body) if l.strip() == "}")]
    body = [l[8:] if l.startswith("        ") else l.strip() for l in body]
    out, cur = [], []
    for l in body + [""]:
        if not l.strip():
            if cur:
                out.append(cur)
            cur = []
            continue
        cur.append(l)
    tops, carry = [], []
    for block in out:
        block, carry = carry + block, []
        i = next((i for i, l in enumerate(block) if not l.startswith(("@", "/", "*", " "))), None)
        if i is None:  # a comment detached by a blank line
            carry = block
            continue
        if not re.match(r"(private|internal|public) ", block[i]):
            block[i] = "private " + re.sub(r"^public ", "", block[i])
        tops.append("\n".join(block))
    return tops


def convert(path, text=None, factory=None, leaf_names=None):
    """factory=(name, params): emit `fun name(params) = funSpec { ... }` into <Name>.kt instead of a spec class;
    leaf_names maps method names to leaf names (default: derived from the method name)."""
    text = text or path.read_text()
    cm = re.search(r"^((?:@[\w.]+(?:\([^)]*\))?\s*\n)*)(?:internal |open |abstract )?class (\w+)(\s*:[^{]*)?\s*\{\n", text, re.M)
    if not cm:
        return f"SKIP {path}: no class"
    if cm.group(3) and cm.group(3).strip(": \n"):
        return f"SKIP {path}: class has supertypes ({cm.group(3).strip()})"
    suppressions = set()
    for a, args in re.findall(r"@([\w.]+)(\([^)]*\))?", cm.group(1)):
        if a != "Suppress":
            return f"SKIP {path}: annotated class (@{a})"
        suppressions.update(x.strip() for x in args.strip("()").split(","))
    cls = cm.group(2)
    end = block_end(text, cm.end())
    body, after = text[cm.end() : end - 1], text[end:]
    if after.strip():
        return f"SKIP {path}: content after class"
    leaves, members, hooks, optins, checks, tops, getters = [], [], [], set(), [], [], []
    lines = body.split("\n")
    starts = [i for i, l in enumerate(lines) if re.match(r"    [^ })\]\n]", l) and not re.match(r"    @", lines[i - 1] if i else "")]
    starts = [i for i in starts if not (i and re.match(r"    @", lines[i - 1]))]
    # group annotation lines with the declaration below them
    items, i = [], 0
    while i < len(lines):
        if re.match(r"    [^ })\]\n]", lines[i]):
            j = i
            while re.match(r"    @", lines[j]) and j + 1 < len(lines):
                j += 1
            k = j + 1
            while k < len(lines) and not re.match(r"    [^ )\]\n]", lines[k]):
                k += 1
            if k < len(lines) and lines[k].startswith("    }"):
                k += 1
                while k < len(lines) and not re.match(r"    [^ )\]\n]", lines[k]):
                    k += 1
            items.append("\n".join(lines[i:k]).rstrip())
            i = k
        else:
            i += 1
    pending = ""
    for item in items:
        if re.match(r"    (/\*|//)", item):
            pending += item + "\n"
            continue
        comment, pending = pending, ""
        m = re.match(r"((?:    @[\w.]+(?:\([^)]*\))?[^\n]*\n)*)    (.*)", item, re.S)
        ann, decl = m.group(1), m.group(2)
        anns = re.findall(r"@([\w.]+)(\([^)]*\))?", ann)
        names = {a for a, _ in anns}
        for a, args in anns:
            if a == "OptIn":
                optins.update(x.strip() for x in args.strip("()").split(","))
        fm = re.match(r"fun (`[^`]+`|\w+)\(\)(?::\s*Unit)?\s*(=|\{)", decl)
        if "Test" in names:
            if not fm:
                return f"SKIP {path}: odd test declaration {decl[:60]!r}"
            if names - {"Test", "Ignore", "OptIn", "JsName", "Suppress"}:
                return f"SKIP {path}: method {fm.group(1)} annotated {names}"
            for a, args in anns:
                if a == "Suppress":
                    suppressions.update(x.strip() for x in args.strip("()").split(","))
            rest = decl[fm.end():]
            inner = rest.rsplit("}", 1)[0] if fm.group(2) == "{" else "\n    " + rest.strip() + "\n"
            leaves.append((fm.group(1).strip("`"), "Ignore" in names, inner, comment))
        elif names & {"BeforeTest", "AfterTest"}:
            if not fm:
                return f"SKIP {path}: odd hook {decl[:60]!r}"
            members.append(re.sub(r"^(private |internal )", "", decl))
            hooks.append(("beforeTest" if "BeforeTest" in names else "afterTest", fm.group(1)))
        else:
            if names - {"OptIn", "Suppress", "JsName"}:
                return f"SKIP {path}: member annotated {names}"
            d = re.sub(r"^((?:private|internal|override) )+", "", decl)
            kept = "".join(l.strip() + "\n    " for l in ann.split("\n") if l.strip().startswith(("@Suppress", "@OptIn")))
            d = kept + d  # continuation lines are re-indented by stripping 4 columns
            if re.match(r"(private )?companion\b", decl):
                tops.extend(companion_members(decl))
                continue
            if re.match(r"(lateinit )?var\b", d) or re.search(r"=\s*(mutable\w*Of|ArrayList|HashMap|HashSet)\(", d.split("\n")[0]):
                checks.append(d.split("\n")[0])
            gm = re.match(r"val (\w+)(\s*:[^=\n]+?)?\s*(?:\n\s*)?get\(\)\s*=", d)
            if gm:  # a local can't have a getter: make it a local function, so it's still re-evaluated on each use
                getters.append(gm.group(1))
                d = f"fun {gm.group(1)}(){gm.group(2) or ''} =" + d[gm.end():]
            if re.match(r"(inline |(?:val|var) [\w<>*?, ]+\.|object |enum |data class |sealed |interface |abstract )", d) or re.search(r"\n\s*(get|set)\(", d):
                tops.append("private " + d)  # can't be local declarations
            else:
                members.append(d)
    for g in getters:
        use = re.compile(rf"(?<![.\w]){g}(?![\w(])")
        members = [d if d.startswith(f"fun {g}()") else use.sub(g + "()", d) for d in members]
        leaves = [(n, i, use.sub(g + "()", inner), c) for n, i, inner, c in leaves]
    if checks and not hooks:
        print(f"CHECK {path}: mutable members without per-test hooks: {checks}", file=sys.stderr)
    for name, _, inner, _ in leaves:
        for raw in re.finditer(r'"""(.*?)"""(\.\w+)?', inner, re.S):
            if "\n" in raw.group(1) and raw.group(2) not in (".trimIndent", ".trimMargin"):
                print(f"WARN {path}: {name} has a raw string not trimmed ({raw.group(2)}); re-indenting changes it", file=sys.stderr)
    if not leaves:
        return f"SKIP {path}: no tests"
    subject = re.sub(r"^Test|Test$", "", cls) or cls
    spec = subject + "Spec"
    out_lines = []
    seen = set()
    for name, ignored, inner, comment in leaves:
        leaf = (leaf_names or {}).get(name) or (leaf_name(name) if not name.count(" ") else name)
        assert leaf not in seen, (path, leaf)
        seen.add(leaf)
        lines = inner.strip("\n").split("\n")
        ind = min((len(l) - len(l.lstrip()) for l in lines if l.strip()), default=0)
        inner_txt = "\n".join(("            " + l[ind:]) if l.strip() else "" for l in lines)
        fn = "xtest" if ignored else "test"
        doc = ""
        if comment:
            ctext = re.sub(r"^\s*/\*+|\*+/\s*$", "", comment.strip(), flags=re.M)
            doc_lines = [re.sub(r"^\s*(\*|//) ?", "", l).rstrip() for l in ctext.strip().split("\n")]
            doc = "".join(f"        // {l}".rstrip() + "\n" for l in doc_lines if l.strip())
        out_lines.append(f'{doc}        {fn}("{leaf}") {{\n{inner_txt}\n        }}')
        pkg = re.search(r"^package ([\w.]+)", text, re.M).group(1)
        if not factory:
            print(json.dumps({"module": str(path).split("/src/")[0], "old_class": f"{pkg}.{cls}", "old_test": name, "new_spec": f"{pkg}.{spec}", "new_case": leaf}))
    header = text[: cm.start()]
    header = re.sub(r"^import kotlin\.test\.(Test|Ignore|BeforeTest|AfterTest)\n", "", header, flags=re.M)
    kotest = "io.kotest.core.spec.style." + ("funSpec" if factory else "FunSpec")
    if "\nimport " in header:
        header = header.replace("\nimport ", f"\nimport {kotest}\nimport ", 1)
    else:
        header = re.sub(r"^(package .*\n)", rf"\1\nimport {kotest}\n", header, count=1, flags=re.M)
    if suppressions:
        header = f"@file:Suppress({', '.join(sorted(suppressions))})\n\n" + header
    if optins:
        header = f"@file:OptIn({', '.join(sorted(optins))})\n\n" + header
    pre = []
    for d in members:
        dl = d.split("\n")
        ind = min((len(l) - len(l.lstrip()) for l in dl[1:] if l.strip()), default=4)
        pre.append("        " + dl[0] + "".join("\n" + ("        " + l[4:] if l.strip() else "") for l in dl[1:]))
    for kind, fn in hooks:
        pre.append(f"        {kind} {{ {fn.strip('`')}() }}")
    if factory:
        name, params = factory
        out = header + f"fun {name}({params}) =\n    funSpec {{\n" + "\n\n".join(pre + out_lines) + "\n    }\n" + "".join("\n" + d + "\n" for d in tops)
        spec = name[0].upper() + name[1:]
    else:
        out = header + f"class {spec} :\n    FunSpec({{\n" + "\n\n".join(pre + out_lines) + "\n    })\n" + "".join("\n" + d + "\n" for d in tops)
    target = path.with_name(spec + ".kt")
    if target.exists() and target != path:
        return f"SKIP {path}: {target.name} exists"
    if not dry:
        path.unlink()
        target.write_text(out)
    return f"OK {path} -> {target.name} ({len(leaves)} leaves)"


if __name__ == "__main__":
    for f in [Path(a) for a in sys.argv[1:] if not a.startswith("--")]:
        print(convert(f), file=sys.stderr)
