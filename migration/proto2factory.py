"""Throwaway (#1017): turns a prototype test class and the specs forwarding to it into a Kotest factory.

Usage: proto2factory.py [--dry] PROTOTYPE.kt FACTORY_NAME SPEC.kt...
The specs (already converted by plain2spec.py) hold `val prototype = PrototypeX(args)` and leaves that only call
`prototype.method()`. The prototype class becomes `fun FACTORY_NAME(params) = funSpec { ... }` with those leaves,
and each spec becomes `include(FACTORY_NAME(args))`. Leaf names stay the specs' ones, so parity is unchanged.
"""
import re, sys
from pathlib import Path
from plain2spec import convert, block_end

dry = "--dry" in sys.argv
args = [a for a in sys.argv[1:] if not a.startswith("--")]
proto, name, specs = Path(args[0]), args[1], [Path(a) for a in args[2:]]

forwards, ctor, hooks = {}, {}, {}
for spec in specs:
    t = spec.read_text()
    m = re.search(r"val prototype(?::\s*\w+)? =\s*\w+(?:\.\w+)*\(", t)
    ctor[spec] = t[m.end() : block_end(t.replace("(", "{").replace(")", "}"), m.end()) - 1].strip().rstrip(",")
    leaves = re.findall(r'(x?test)\("([^"]+)"\) \{\s*prototype\.(\w+)\(\)\s*\}', t)
    assert len(leaves) == len(re.findall(r'\b(x?test)\("', t)), f"{spec}: a leaf does more than forwarding"
    forwards[spec] = {meth: (leaf, fn) for fn, leaf, meth in leaves}
    hooks[spec] = re.findall(r"(beforeTest|afterTest) \{ \w+\(\) \}", t)
first = forwards[specs[0]]
for spec in specs:
    assert forwards[spec] == first, f"{spec} forwards differently from {specs[0]}"
    assert hooks[spec] == hooks[specs[0]], f"{spec} has different hooks"

text = proto.read_text()
cm = re.search(r"^(?:internal |open )?class (\w+)\(", text, re.M)
pend = block_end(text.replace("(", "{").replace(")", "}"), cm.end())
params = text[cm.end() : pend - 1]
params = re.sub(r"(private |internal )?(val|var) ", "", params).strip().rstrip(",")
text = text[: cm.start()] + f"class {cm.group(1)} " + text[pend:].lstrip()
for meth, (leaf, fn) in first.items():
    ann = "    @Test\n" + ("    @Ignore\n" if fn == "xtest" else "")
    text, n = re.subn(rf"^    fun {meth}\(\)", ann + f"    fun {meth}()", text, flags=re.M)
    assert n == 1, (meth, n)
# the specs' hooks call the prototype's same-named methods
for h in hooks[specs[0]]:
    hm = re.search(rf"{h} \{{ (\w+)\(\) \}}", specs[0].read_text()).group(1)
    pm = re.search(rf"\b{hm}\(\)\s*\{{\s*prototype\.(\w+)\(\)", specs[0].read_text()).group(1)
    ann = "    @BeforeTest\n" if h == "beforeTest" else "    @AfterTest\n"
    text = re.sub(rf"^    fun {pm}\(\)", ann + f"    fun {pm}()", text, flags=re.M)
text = re.sub(r"^(    )(?:override )?fun ", r"\1fun ", text, flags=re.M)
if dry:
    print(convert(proto, text, (name, params), {m: l for m, (l, _) in first.items()}).replace(str(proto), "", 0), file=sys.stderr)
    sys.exit()
print(convert(proto, text, (name, params), {m: l for m, (l, _) in first.items()}), file=sys.stderr)
pkg = re.search(r"^package ([\w.]+)", text, re.M).group(1)
for spec in specs:
    t = spec.read_text()
    spec_pkg = re.search(r"^package ([\w.]+)", t, re.M).group(1)
    imports = {l for l in re.findall(r"^import .*$", t, re.M) if l.split()[1].split(".")[-1] in ctor[spec] }
    imports.add("import io.kotest.core.spec.style.FunSpec")
    if spec_pkg != pkg:
        imports.add(f"import {pkg}.{name}")
    head = f"package {spec_pkg}\n\n" + "\n".join(sorted(imports)) + "\n"
    cls = re.search(r"\nclass (\w+)", t).group(1)
    spec.write_text(f"{head}\nclass {cls} :\n    FunSpec({{\n        include(\n            {name}(\n                {ctor[spec]},\n            ),\n        )\n    }})\n")
    print(f"OK {spec}", file=sys.stderr)
