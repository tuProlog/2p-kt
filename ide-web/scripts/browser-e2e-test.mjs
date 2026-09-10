#!/usr/bin/env node
// End-to-end smoke test for the packaged ide-web distribution, driven against a REAL browser via the Chrome
// DevTools Protocol (CDP) rather than jsBrowserTest's karma/mocha unit tests. jsBrowserTest only ever mounts
// individual Kotlin classes (AceEditorView, WebIdeAppWiringTest, ...) in isolation; it never boots index.html,
// so it cannot catch bugs in how those pieces are wired together on the real page — which is exactly the class
// of bug this script exists to catch (see history: a missing SolverFactory import, a shadowed `replaceWith`
// that silently detached DOM nodes, and an `Ace.transformAction` no-op that discarded every keystroke were all
// found this way, not by jsBrowserTest).
//
// Procedure:
//   1. Serve the given distribution directory (the output of `jsBrowserDistribution`) over plain HTTP.
//   2. Launch a real headless Chrome (the same browser jsBrowserTest's karma config already targets) with a
//      fresh, throwaway user-data-dir and CDP enabled.
//   3. Connect to the page over CDP, capture every console message and uncaught exception for the whole run.
//   4. Run a fixed sequence of scenarios against the live app (see SCENARIOS below), each exercising the app
//      the way a person would: typing through Ace's real input pipeline (execCommand('insertText', ...) on
//      its focused textarea, not calling Kotlin setters directly), clicking real buttons, reading back the
//      rendered DOM.
//   5. Report a pass/fail table plus any captured console errors/exceptions, and exit non-zero on any failure.
//
// Requires a local Chrome/Chromium install. Resolution order: $CHROME_BIN, then common per-OS install paths
// (the same convention karma-chrome-launcher uses for jsBrowserTest, so if that task works here, this will too).
//
// Usage: node browser-e2e-test.mjs <path-to-jsBrowserDistribution-output>

import { spawn } from "node:child_process";
import { createServer } from "node:http";
import { readFile, mkdtemp, rm, readdir } from "node:fs/promises";
import { existsSync, readFileSync } from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";

const CHROME_CANDIDATES = [
  process.env.CHROME_BIN,
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
  "/Applications/Chromium.app/Contents/MacOS/Chromium",
  "/usr/bin/google-chrome-stable",
  "/usr/bin/google-chrome",
  "/usr/bin/chromium-browser",
  "/usr/bin/chromium",
].filter(Boolean);

const MIME_TYPES = {
  ".html": "text/html",
  ".js": "text/javascript",
  ".css": "text/css",
  ".png": "image/png",
  ".map": "application/json",
};

function resolveChromeBinary() {
  const found = CHROME_CANDIDATES.find((p) => existsSync(p));
  if (!found) {
    throw new Error(
      `No Chrome/Chromium binary found. Checked: ${CHROME_CANDIDATES.join(", ")}. Set $CHROME_BIN to override.`,
    );
  }
  return found;
}

function serveStatic(rootDir) {
  const server = createServer(async (req, res) => {
    const filePath = path.join(rootDir, decodeURIComponent(new URL(req.url, "http://localhost").pathname));
    try {
      const data = await readFile(filePath);
      const ext = path.extname(filePath);
      res.writeHead(200, { "Content-Type": MIME_TYPES[ext] ?? "application/octet-stream" });
      res.end(data);
    } catch {
      res.writeHead(404);
      res.end("not found");
    }
  });
  return new Promise((resolve) => {
    server.listen(0, "127.0.0.1", () => resolve({ server, port: server.address().port }));
  });
}

async function waitForFile(filePath, timeoutMs) {
  const deadline = Date.now() + timeoutMs;
  while (Date.now() < deadline) {
    if (existsSync(filePath) && readFileSync(filePath, "utf8").includes("\n")) return;
    await new Promise((r) => setTimeout(r, 50));
  }
  throw new Error(`Timed out waiting for ${filePath}`);
}

async function launchChrome() {
  const userDataDir = await mkdtemp(path.join(tmpdir(), "ide-web-e2e-"));
  const chromeProcess = spawn(
    resolveChromeBinary(),
    [
      "--headless=new",
      "--disable-gpu",
      "--no-sandbox",
      "--no-first-run",
      "--no-default-browser-check",
      "--remote-debugging-port=0",
      `--user-data-dir=${userDataDir}`,
      "about:blank",
    ],
    { stdio: "ignore" },
  );
  const devtoolsFile = path.join(userDataDir, "DevToolsActivePort");
  await waitForFile(devtoolsFile, 10_000);
  const [portLine, wsPath] = readFileSync(devtoolsFile, "utf8").split("\n");
  return { chromeProcess, userDataDir, cdpBase: `http://127.0.0.1:${portLine}` };
}

/** Minimal CDP client: enough of Runtime/Page to drive Runtime.evaluate and collect console/exception events. */
async function connectCdp(cdpBase, pageUrl) {
  const target = await (await fetch(`${cdpBase}/json/new?${encodeURIComponent(pageUrl)}`, { method: "PUT" })).json();
  const ws = new WebSocket(target.webSocketDebuggerUrl);
  await new Promise((resolve, reject) => {
    ws.addEventListener("open", resolve, { once: true });
    ws.addEventListener("error", reject, { once: true });
  });

  let nextId = 0;
  const pending = new Map();
  const consoleMessages = [];
  const exceptions = [];

  ws.addEventListener("message", (ev) => {
    const msg = JSON.parse(ev.data);
    if (msg.id !== undefined && pending.has(msg.id)) {
      pending.get(msg.id)(msg.result);
      pending.delete(msg.id);
      return;
    }
    if (msg.method === "Runtime.consoleAPICalled") {
      consoleMessages.push({
        type: msg.params.type,
        text: msg.params.args.map((a) => a.value ?? a.description ?? "").join(" "),
      });
    }
    if (msg.method === "Runtime.exceptionThrown") {
      exceptions.push(msg.params.exceptionDetails.text ?? JSON.stringify(msg.params.exceptionDetails));
    }
  });

  function send(method, params = {}) {
    const id = ++nextId;
    return new Promise((resolve) => {
      pending.set(id, resolve);
      ws.send(JSON.stringify({ id, method, params }));
    });
  }

  await send("Runtime.enable");
  await send("Page.enable");

  /** Evaluates a JS expression in the page and returns its `.result.value` (must be JSON-serializable). */
  async function evalJs(expression) {
    const { result, exceptionDetails } = await send("Runtime.evaluate", {
      expression,
      returnByValue: true,
      awaitPromise: true,
    });
    if (exceptionDetails) {
      const detail = exceptionDetails.exception?.description ?? exceptionDetails.exception?.value ?? "";
      throw new Error(`Runtime.evaluate failed: ${exceptionDetails.text} ${detail}`);
    }
    return result.value;
  }

  async function navigate(url) {
    await send("Page.navigate", { url });
  }

  return { evalJs, navigate, send, consoleMessages, exceptions, close: () => ws.close() };
}

/** Polls the async `get()` function until `predicate(value)` holds, or `timeoutMs` elapses. */
async function pollUntil(get, predicate, timeoutMs, intervalMs = 100) {
  const deadline = Date.now() + timeoutMs;
  let last;
  while (Date.now() < deadline) {
    last = await get();
    if (predicate(last)) return last;
    await new Promise((r) => setTimeout(r, intervalMs));
  }
  return last;
}

const SHELL_IDS = [
  "btn-new", "templates-select", "btn-open", "btn-save", "btn-save-as", "btn-close-page",
  "status-label", "tab-bar", "query-input", "solve-button", "solve10-button", "solve-all-button", "stop-button",
  "reset-button", "timeout-input", "editor", "side", "side-tab-bar",
];

const SCENARIOS = [
  {
    name: "app boots with the full static shell and no console errors",
    async run({ evalJs, exceptions }) {
      // The shell itself is static markup and always present immediately; wait for the Ace editor textarea
      // instead, since that only exists once WebIdeView has actually finished constructing and mounting.
      await pollUntil(() => evalJs(`!!document.querySelector('textarea.ace_text-input')`), (v) => v, 10_000);
      const result = await evalJs(`
        (function() {
          const missing = ${JSON.stringify(SHELL_IDS)}.filter(id => !document.getElementById(id));
          const cssLoaded = getComputedStyle(document.querySelector('.menu-bar')).display === 'flex';
          return { missing, cssLoaded };
        })()
      `);
      const failures = [];
      if (result.missing.length > 0) failures.push(`missing elements: ${result.missing.join(", ")}`);
      if (!result.cssLoaded) failures.push("ide-web.css does not appear to be applied");
      if (exceptions.length > 0) failures.push(`uncaught exceptions during boot: ${exceptions.join(" | ")}`);
      return failures;
    },
  },
  {
    name: "the page title reports the :core version",
    async run({ evalJs }) {
      const title = await evalJs(`document.title`);
      return /\d+\.\d+\.\d+/.test(title) ? [] : [`expected a version number in the page title, got: "${title}"`];
    },
  },
  {
    name: "the redundant 'New scratch' button was removed",
    async run({ evalJs }) {
      const exists = await evalJs(`!!document.getElementById('btn-new-scratch')`);
      return exists ? ["#btn-new-scratch is still present; the plain New button should be the only one"] : [];
    },
  },
  {
    name: "pressing Enter in the query field triggers solve",
    async run({ evalJs }) {
      await evalJs(`
        (function() {
          const queryInput = document.getElementById('query-input');
          queryInput.focus();
          queryInput.value = 'true';
          queryInput.dispatchEvent(new Event('input', { bubbles: true }));
          queryInput.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));
        })()
      `);
      const status = await pollUntil(
        () => evalJs(`document.getElementById('status-label').textContent`),
        (v) => /COMPLETED|FAILED|RUNNING|AWAITING/.test(v),
        3000,
      );
      return /COMPLETED|FAILED|RUNNING|AWAITING/.test(status)
        ? []
        : [`expected Enter to trigger a solve, status stayed: "${status}"`];
    },
  },
  {
    name: "typing goes through Ace's real insert pipeline",
    async run({ evalJs }) {
      const text = "p(1).\np(2).\n";
      await evalJs(`
        (function() {
          const textarea = document.querySelector('textarea.ace_text-input');
          textarea.focus();
          for (const ch of ${JSON.stringify(text)}) document.execCommand('insertText', false, ch);
        })()
      `);
      const lines = await pollUntil(
        () => evalJs(`Array.from(document.querySelectorAll('.ace_line')).map(e => e.textContent).join('\\n')`),
        (v) => v.length > 0,
        3000,
      );
      return lines === text.trimEnd() + "\n" || lines === text ? [] : [`expected typed text to appear, got: ${JSON.stringify(lines)}`];
    },
  },
  {
    name: "solving a query with multiple facts yields all solutions",
    async run({ evalJs }) {
      await evalJs(`
        (function() {
          const textarea = document.querySelector('textarea.ace_text-input');
          textarea.focus();
          document.execCommand('selectAll');
          document.execCommand('delete');
          for (const ch of "f(1).\\nf(2).\\nf(3).\\n") document.execCommand('insertText', false, ch);
          const queryInput = document.getElementById('query-input');
          queryInput.focus();
          queryInput.value = 'f(X)';
          queryInput.dispatchEvent(new Event('input', { bubbles: true }));
        })()
      `);
      await new Promise((r) => setTimeout(r, 500));
      await evalJs(`document.getElementById('solve-all-button').click()`);
      const status = await pollUntil(
        () => evalJs(`document.getElementById('status-label').textContent`),
        (v) => /COMPLETED|FAILED|CANCELLED/.test(v),
        5000,
      );
      const solutionsText = await evalJs(`
        (function() {
          const panel = Array.from(document.querySelectorAll('.side-content')).find(d => d.querySelector('ul.solutions'));
          return panel ? panel.textContent : null;
        })()
      `);
      const failures = [];
      if (!/COMPLETED/.test(status)) failures.push(`resolution ended as "${status}" instead of COMPLETED`);
      for (const expected of ["X = 1", "X = 2", "X = 3"]) {
        if (!solutionsText?.includes(expected)) failures.push(`solutions panel missing "${expected}": ${solutionsText}`);
      }
      return failures;
    },
  },
  {
    name: "side panels stay attached to the DOM and toggle on click",
    async run({ evalJs }) {
      const count = await evalJs(`document.querySelectorAll('.side-content').length`);
      const failures = [];
      if (count !== 11) failures.push(`expected 11 .side-content panels, found ${count}`);
      const toggled = await evalJs(`
        (function() {
          const tabs = Array.from(document.querySelectorAll('.side-tab'));
          const operatorsTab = tabs.find(t => t.textContent === 'Operators');
          operatorsTab.click();
          return operatorsTab.className.includes('selected');
        })()
      `);
      if (!toggled) failures.push("clicking the Operators side-tab did not mark it selected");
      return failures;
    },
  },
  {
    name: "clicking a table header sorts the Operators table by that column",
    async run({ evalJs }) {
      const namesFor = (dir) =>
        evalJs(`
          (function() {
            const operatorsTab = Array.from(document.querySelectorAll('.side-tab')).find(t => t.textContent === 'Operators');
            operatorsTab.click();
            // Sorting rebuilds the <table> from scratch, so the header/rows must be re-queried AFTER the click,
            // not read off a reference captured beforehand (which would go stale, still attached to the old,
            // now-detached table).
            if (${dir === "click" ? "true" : "false"}) {
              document.querySelector('.side-content table').querySelectorAll('th')[0].click();
            }
            const table = document.querySelector('.side-content table');
            return Array.from(table.querySelectorAll('tr')).slice(1).map(r => r.cells[0].textContent);
          })()
        `);

      const initial = await namesFor("none");
      const ascending = await namesFor("click"); // first click: sort ascending
      const descending = await namesFor("click"); // second click: toggle to descending
      const failures = [];
      const sortedAsc = [...ascending].sort();
      if (JSON.stringify(ascending) !== JSON.stringify(sortedAsc)) {
        failures.push(`expected ascending order after one click, got: ${JSON.stringify(ascending)}`);
      }
      if (JSON.stringify(descending) !== JSON.stringify([...sortedAsc].reverse())) {
        failures.push(`expected descending order after a second click, got: ${JSON.stringify(descending)}`);
      }
      if (initial.length < 2) failures.push("need at least 2 operators to prove sorting reordered anything");
      return failures;
    },
  },
  {
    name: "the unknown flag is editable and its new value survives a re-render",
    async run({ evalJs }) {
      // Flags only exist once a solver session does; the earlier "solving a query" scenario already solved.
      const before = await evalJs(`
        (function() {
          const flagsTab = Array.from(document.querySelectorAll('.side-tab')).find(t => t.textContent === 'Flags');
          flagsTab.click();
          const row = Array.from(document.querySelectorAll('.side-content table tr'))
            .find(r => r.cells[0]?.textContent === 'unknown');
          return row ? { value: row.cells[1].querySelector('select')?.value, options: row.cells[1].querySelector('select')?.value !== undefined } : null;
        })()
      `);
      if (!before) return ["no 'unknown' flag row found in the Flags table (did the earlier solve scenario run?)"];
      if (!before.options) return [`expected a <select> for the 'unknown' flag, found none (value read: ${before.value})`];

      const target = before.value === "fail" ? "error" : "fail";
      await evalJs(`
        (function() {
          const row = Array.from(document.querySelectorAll('.side-content table tr'))
            .find(r => r.cells[0]?.textContent === 'unknown');
          const select = row.cells[1].querySelector('select');
          select.value = ${JSON.stringify(target)};
          select.dispatchEvent(new Event('change', { bubbles: true }));
        })()
      `);
      // Changing a flag invalidates the current solver session (it must be rebuilt to pick up the new
      // option), so the Flags panel only reflects the new value once a fresh session exists after solving.
      await new Promise((r) => setTimeout(r, 300));
      await evalJs(`document.getElementById('solve-all-button').click()`);
      await pollUntil(
        () => evalJs(`document.getElementById('status-label').textContent`),
        (v) => /COMPLETED|FAILED/.test(v),
        5000,
      );
      const after = await pollUntil(
        () =>
          evalJs(`
            (function() {
              const row = Array.from(document.querySelectorAll('.side-content table tr'))
                .find(r => r.cells[0]?.textContent === 'unknown');
              return row?.cells[1].querySelector('select')?.value;
            })()
          `),
        (v) => v === target,
        3000,
      );
      return after === target ? [] : [`expected the 'unknown' flag to re-render as "${target}", got "${after}"`];
    },
  },
  {
    name: "loading a template applies syntax coloring without requiring an edit",
    async run({ evalJs }) {
      const hasTemplates = await evalJs(`!document.getElementById('templates-select').hidden`);
      if (!hasTemplates) return []; // nothing to check when the app was built with no templates
      await evalJs(`
        (function() {
          const select = document.getElementById('templates-select');
          select.selectedIndex = 1;
          select.dispatchEvent(new Event('change', { bubbles: true }));
        })()
      `);
      await new Promise((r) => setTimeout(r, 500));
      const coloredSpanCount = await evalJs(`
        document.querySelectorAll('.ace_line [class^="ace_"]:not([class="ace_line"])').length
      `);
      return coloredSpanCount > 0 ? [] : ["no colored (ace_*) spans found after loading a template"];
    },
  },
  {
    name: "uploading a real file opens it as a new page",
    async run({ evalJs }) {
      const tabCountBefore = await evalJs(`document.getElementById('tab-bar').children.length`);
      // A real OS file picker can't be scripted; construct a File in-page and inject it into the hidden
      // <input type=file>, the standard trick for testing file inputs without one.
      await evalJs(`
        (function() {
          const input = document.getElementById('upload-input');
          const file = new File(["up(1).\\nup(2)."], "uploaded.pl", { type: "text/plain" });
          const dt = new DataTransfer();
          dt.items.add(file);
          input.files = dt.files;
          input.dispatchEvent(new Event('change', { bubbles: true }));
        })()
      `);
      const lines = await pollUntil(
        () => evalJs(`Array.from(document.querySelectorAll('.ace_line')).map(e => e.textContent).join('\\n')`),
        (v) => v.includes("up(1)"),
        3000,
      );
      const tabCountAfter = await evalJs(`document.getElementById('tab-bar').children.length`);
      const failures = [];
      if (!lines.includes("up(1).") || !lines.includes("up(2).")) {
        failures.push(`expected the uploaded content in the editor, got: ${JSON.stringify(lines)}`);
      }
      if (tabCountAfter !== tabCountBefore + 1) {
        failures.push(`expected a new tab to open for the uploaded file (${tabCountBefore} -> ${tabCountAfter})`);
      }
      return failures;
    },
  },
  {
    name: "downloading the selected page saves its real content to disk",
    async run({ evalJs, send }) {
      const downloadDir = await mkdtemp(path.join(tmpdir(), "ide-web-e2e-download-"));
      try {
        await send("Browser.setDownloadBehavior", {
          behavior: "allow",
          downloadPath: downloadDir,
          eventsEnabled: true,
        });
        const editorText = await evalJs(
          `Array.from(document.querySelectorAll('.ace_line')).map(e => e.textContent).join('\\n')`,
        );
        await evalJs(`document.getElementById('btn-download').click()`);
        const fileName = await pollUntil(
          async () => (await readdir(downloadDir)).find((f) => !f.endsWith(".crdownload")),
          (v) => v !== undefined,
          3000,
        );
        if (!fileName) return ["no file appeared in the download directory"];
        const downloaded = await readFile(path.join(downloadDir, fileName), "utf8");
        return downloaded === editorText
          ? []
          : [`downloaded content did not match the editor: ${JSON.stringify(downloaded)}`];
      } finally {
        await rm(downloadDir, { recursive: true, force: true });
      }
    },
  },
  {
    name: "Ctrl+wheel and Ctrl+/- zoom the editor's font size",
    async run({ evalJs }) {
      const fontSizeOf = () => evalJs(`parseFloat(getComputedStyle(document.getElementById('editor')).fontSize)`);
      const initial = await fontSizeOf();

      await evalJs(`
        document.getElementById('editor').dispatchEvent(
          new KeyboardEvent('keydown', { key: '+', ctrlKey: true, bubbles: true })
        )
      `);
      const afterKeyZoomIn = await fontSizeOf();

      await evalJs(`
        document.getElementById('editor').dispatchEvent(
          new WheelEvent('wheel', { deltaY: -100, ctrlKey: true, bubbles: true, cancelable: true })
        )
      `);
      const afterWheelZoomIn = await fontSizeOf();

      await evalJs(`
        document.getElementById('editor').dispatchEvent(
          new KeyboardEvent('keydown', { key: '0', ctrlKey: true, bubbles: true })
        )
      `);
      const afterReset = await fontSizeOf();

      const failures = [];
      if (!(afterKeyZoomIn > initial)) failures.push(`Ctrl+'+' did not increase font size (${initial} -> ${afterKeyZoomIn})`);
      if (!(afterWheelZoomIn > afterKeyZoomIn)) failures.push(`Ctrl+wheel-up did not further increase font size (${afterKeyZoomIn} -> ${afterWheelZoomIn})`);
      if (afterReset !== initial) failures.push(`Ctrl+'0' did not reset font size to ${initial}, got ${afterReset}`);
      return failures;
    },
  },
  {
    name: "dragging the split handle resizes the side panel",
    async run({ evalJs }) {
      const before = await evalJs(`document.getElementById('side').getBoundingClientRect().width`);
      await evalJs(`
        (function() {
          const handle = document.getElementById('split-handle');
          const rect = handle.getBoundingClientRect();
          const x = rect.left + rect.width / 2;
          handle.dispatchEvent(new MouseEvent('mousedown', { clientX: x, bubbles: true }));
          window.dispatchEvent(new MouseEvent('mousemove', { clientX: x - 100, bubbles: true }));
          window.dispatchEvent(new MouseEvent('mouseup', { bubbles: true }));
        })()
      `);
      const after = await evalJs(`document.getElementById('side').getBoundingClientRect().width`);
      // Dragging left by 100px should widen the (right-hand) side panel by roughly that much.
      return after > before + 50 ? [] : [`expected the side panel to widen by ~100px, went ${before} -> ${after}`];
    },
  },
  {
    name: "the UI and the Ace editor follow the OS/browser color scheme",
    async run({ evalJs, send }) {
      const failures = [];
      const readColors = () =>
        evalJs(`
          (function() {
            return {
              bodyBg: getComputedStyle(document.body).backgroundColor,
              aceClass: document.querySelector('.ace_editor').className,
            };
          })()
        `);

      await send("Emulation.setEmulatedMedia", { features: [{ name: "prefers-color-scheme", value: "light" }] });
      await new Promise((r) => setTimeout(r, 200));
      const light = await readColors();
      if (!light.aceClass.includes("ace-github") || light.aceClass.includes("dark")) {
        failures.push(`expected the light Ace theme, got class "${light.aceClass}"`);
      }

      await send("Emulation.setEmulatedMedia", { features: [{ name: "prefers-color-scheme", value: "dark" }] });
      await new Promise((r) => setTimeout(r, 200));
      const dark = await readColors();
      if (!dark.aceClass.includes("dark")) failures.push(`expected the dark Ace theme, got class "${dark.aceClass}"`);
      if (dark.bodyBg === light.bodyBg) failures.push(`body background did not change between color schemes: ${dark.bodyBg}`);

      await send("Emulation.setEmulatedMedia", { features: [] });
      return failures;
    },
  },
];

async function main() {
  const distDir = process.argv[2];
  if (!distDir || !existsSync(path.join(distDir, "index.html"))) {
    console.error("Usage: node browser-e2e-test.mjs <path-to-jsBrowserDistribution-output>");
    process.exit(2);
  }

  const { server, port } = await serveStatic(distDir);
  const { chromeProcess, userDataDir, cdpBase } = await launchChrome();
  const pageUrl = `http://127.0.0.1:${port}/index.html`;
  const cdp = await connectCdp(cdpBase, pageUrl);

  try {
    await cdp.navigate(pageUrl);
    const results = [];
    for (const scenario of SCENARIOS) {
      let failures;
      try {
        failures = await scenario.run(cdp);
      } catch (e) {
        failures = [`threw: ${e.message}`];
      }
      results.push({ name: scenario.name, failures });
      console.log(`${failures.length === 0 ? "PASS" : "FAIL"}  ${scenario.name}`);
      failures.forEach((f) => console.log(`      - ${f}`));
    }

    if (cdp.consoleMessages.some((m) => m.type === "error")) {
      console.log("\nConsole errors captured during the run:");
      cdp.consoleMessages.filter((m) => m.type === "error").forEach((m) => console.log(`  [${m.type}] ${m.text}`));
    }
    if (cdp.exceptions.length > 0) {
      console.log("\nUncaught page exceptions captured during the run:");
      cdp.exceptions.forEach((e) => console.log(`  ${e}`));
    }

    const failedCount = results.filter((r) => r.failures.length > 0).length;
    console.log(`\n${results.length - failedCount}/${results.length} scenarios passed.`);
    process.exitCode = failedCount > 0 ? 1 : 0;
  } finally {
    cdp.close();
    chromeProcess.kill();
    await new Promise((resolve) => {
      chromeProcess.once("exit", resolve);
      setTimeout(resolve, 2000); // don't hang forever if the process is already gone
    });
    server.close();
    await rm(userDataDir, { recursive: true, force: true, maxRetries: 5, retryDelay: 100 });
  }
}

main().catch((e) => {
  console.error("Test harness error:", e);
  process.exit(2);
});
