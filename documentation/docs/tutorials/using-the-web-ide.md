# Using the Web IDE

This tutorial takes you from a blank editor to a working, queried Prolog knowledge base, entirely in your
browser: no install, no compiler, no terminal. It builds the same family-tree example as the
[Kotlin](getting-started-kotlin.md)/[Java](getting-started-java.md)/[JavaScript](getting-started-js.md)
tutorials, but here you *are* the "program" — typing Prolog directly into the editor and clicking buttons
instead of writing host-language code.

## 1. Open the Web IDE

Go to **[tuprolog.github.io/2p-kt/web-ide/](https://tuprolog.github.io/2p-kt/web-ide/)**. The app boots with
one untitled, empty page already open — a text editor on the left, a query bar above it, and a tabbed panel on
the right.

(If you'd rather run it locally instead of using the hosted copy, see
[Deploy the Web IDE](../how-to/deploy-the-web-ide.md).)

## 2. Write a theory

Click into the editor and type:

```prolog
parent(abraham, isaac).
parent(isaac, jacob).
parent(jacob, joseph).

ancestor(X, Y) :- parent(X, Y).
ancestor(X, Y) :- parent(X, Z), ancestor(Z, Y).
```

As you type, the editor highlights syntax (atoms, variables, operators, comments, ...) using 2P-Kt's own
lexer/parser — the exact same one the JVM tooling uses — not a set of hand-maintained regex rules. Nothing to
click here; this is automatic and continuous.

## 3. Run a query

In the query field above the editor, type:

```prolog
ancestor(abraham, X).
```

Press <kbd>Enter</kbd>, or click **Solve**. The *Solutions* tab (already selected, on the right) fills in with
the first answer: `X = isaac`. Click **Solve** again — now relabeled **Next** — to step through the remaining
answers one at a time (`X = jacob`, then `X = joseph`), or click **Solve all** instead to fetch every remaining
answer in one go. **Solve 10**/**Solve 100** sit in between: they fetch up to that many further solutions per
click, useful for a query with many but not literally unbounded answers.

## 4. Break it on purpose, and see what happens

Delete the `.` at the end of the first line, so it reads just `parent(abraham, isaac)`, then look at the
editor: the line gets a squiggly underline, and hovering over it shows an error tooltip. Switch to the
*Diagnostics* tab on the right — the same error is listed there too, with a line/column location (one-based,
matching the line numbers you'd count by eye in the editor). Put the `.` back and the underline and the
Diagnostics entry both disappear on the next keystroke.

## 5. Inspect what the solver is doing

A few other tabs are worth a look once you've solved at least one query (they only populate once a solver
session exists):

- **Operators** — the operator table in effect for this page (`:-`, `,`, arithmetic operators, ...), editable:
  add a row to define your own.
- **Flags** — the solver's current flag values (`double_quotes`, `unknown`, ...), editable where the flag
  permits it.
- **Libraries** — which `Library`s are loaded and what they contribute.
- **Static KB** / **Dynamic KB** — a read-only view of the clauses currently known to the solver, split
  between what you wrote in the editor (static) and what assertions/retractions added at runtime (dynamic).

Any tab that changes as a side effect of solving (Solutions, Stdout, Stderr, Warnings, Diagnostics) gets a
small dot next to its name until you look at it, so you notice updates in a tab you're not currently viewing.

## 6. Save your work

Click **Save** (or **Save as...** the first time) to persist the page. This writes to your browser's own local
storage — it survives closing the tab and reloading the page, but it never leaves your machine and isn't tied
to a real file. If you want an actual `.pl` file on disk instead, use **Download**; to load one back in, use
**Upload...**. See [Web IDE](../reference/web-ide.md#persistence) for exactly how each of these differs.

## Going further

- [Web IDE](../reference/web-ide.md) documents every button, tab, and keyboard shortcut in full, including the
  built-in theory templates the **New from template…** dropdown offers.
- [Web IDE architecture](../explanation/web-ide-architecture.md) explains how the same editing/diagnostics/
  solving logic you just used is shared, byte-for-byte, with the desktop IDE.
- [Deploy the Web IDE](../how-to/deploy-the-web-ide.md) covers self-hosting it instead of using the copy at
  `tuprolog.github.io`.
- If you'd rather drive 2P-Kt from code instead of a GUI, see the
  [Kotlin](getting-started-kotlin.md)/[Java](getting-started-java.md)/[JavaScript](getting-started-js.md)
  tutorials — they build this exact same family-tree example.
