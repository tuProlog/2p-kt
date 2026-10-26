"""Throwaway (#1017): converts the DSL scope tests (subclasses of AbstractLogicProgrammingScopeTest[WithTheories])
into specs whose leaves run inside `with(<Tester><S> { scope }) { ... }`. Usage: scope2spec.py FILE..."""
import re, sys
from pathlib import Path
from plain2spec import convert

for path in map(Path, sys.argv[1:]):
    t = path.read_text()
    m = re.search(r"class (\w+) :\s*(AbstractLogicProgrammingScopeTest(WithTheories)?)<(.+?)>\(\) \{\n", t)
    tester = "LogicProgrammingScopeWithTheoriesTester" if m.group(3) else "LogicProgrammingScopeTester"
    c = re.search(r"\n    override fun createLogicProgrammingScope\(\): [^=]+=\s*(.+?)\n\n", t, re.S)
    t = t[: m.start()] + f"class {m.group(1)} {{\n" + t[m.end() : c.start()] + "\n" + t[c.end() :]
    t = re.sub(r"^import it\.unibo\.tuprolog\.dsl\.(theory\.)?AbstractLogicProgrammingScopeTest\w*\n", "", t, flags=re.M)
    res = convert(path, t)
    print(res, file=sys.stderr)
    out = path.with_name(re.search(r"-> (\S+)", res).group(1))
    o = out.read_text()
    o = o.replace("    FunSpec({\n", f"    FunSpec({{\n        with({tester}<{m.group(4)}> {{ {c.group(1).strip()} }}) {{\n", 1)
    i = o.rindex("\n    })\n")
    o = o[:i] + "\n        }" + o[i:]
    if tester == "LogicProgrammingScopeTester" and ".dsl\n" not in o and not o.startswith("package it.unibo.tuprolog.dsl\n"):
        o = o.replace("import io.kotest.core.spec.style.FunSpec\n", "import io.kotest.core.spec.style.FunSpec\nimport it.unibo.tuprolog.dsl.LogicProgrammingScopeTester\n", 1)
    out.write_text(o)
