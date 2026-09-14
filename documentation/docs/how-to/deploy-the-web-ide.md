# Deploy the Web IDE

How to get a working copy of the Web IDE running — either by using the one 2P-Kt already hosts, or by
self-hosting it from a release asset or from source. See [Using the Web IDE](../tutorials/using-the-web-ide.md)
for a walkthrough of the app itself once it's running, and [Web IDE](../reference/web-ide.md) for what every
button and tab does.

## 1. Just use the hosted copy

Unless you specifically need a private or customized deployment, this is the whole recipe: open
**[tuprolog.github.io/2p-kt/web-ide/](https://tuprolog.github.io/2p-kt/web-ide/)**. It's the project's own
production build, kept up to date by CI and published alongside the rest of the
[documentation site](https://tuprolog.github.io/2p-kt/) — nothing to install.

## 2. Self-host a released version

The page of the [latest release](https://github.com/tuProlog/2p-kt/releases/latest) (or any past one) exposes
an asset named:

```
ide-web-VERSION.zip
```

Download and unzip it, then serve the resulting folder with any static file server — for example:

```bash
cd ide-web-VERSION
python3 -m http.server 8000
# or: npx serve .
```

and open `http://localhost:8000/` in a browser. Serving it, even just locally, is the same path the project's
own tooling always uses (see [Verify the deployment](#4-verify-the-deployment) below) — prefer it over opening
`index.html` directly via a `file://` URL.

## 3. Build it yourself from source

If you need a version that isn't released yet (e.g. an in-progress branch), build the distribution directly:

```bash
git clone https://github.com/tuProlog/2p-kt.git
cd 2p-kt
./gradlew :ide-web:jsBrowserDistribution
```

The static site lands in `ui/ide-web/build/dist/js/productionExecutable/` (a plain `index.html` plus its JS/CSS/
icon assets — the exact same output the `ide-web-VERSION.zip` release asset is a zip of, see
`:ide-web:zipWebDistribution` in `ui/ide-web/build.gradle.kts`). Serve that folder the same way as step 2.

## 4. Verify the deployment

2P-Kt's own CI doesn't just build the distribution — it drives it with a real, headless Chrome to catch
integration bugs that unit tests can't (see [Web IDE architecture](../explanation/web-ide-architecture.md#two-tier-testing)
for why). You can run the exact same check against your own deployment:

```bash
node ui/ide-web/scripts/browser-e2e-test.mjs ui/ide-web/build/dist/js/productionExecutable
```

(requires a local Chrome/Chromium install; point `$CHROME_BIN` at it if it's not in one of the script's default
locations). A clean run prints one `PASS` line per scenario and exits `0`.

## Embedding notes

The app is a fully static, self-contained single-page app with no server-side component and no required
environment variables or configuration — every asset is relative to `index.html`, so the whole folder can be
served from any subpath (as it already is, under `/web-ide/` on the docs site) without rebuilding it.
