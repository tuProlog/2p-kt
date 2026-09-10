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
import { readFile, mkdtemp, rm } from "node:fs/promises";
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

  return { evalJs, navigate, consoleMessages, exceptions, close: () => ws.close() };
}

async function pollUntil(evalJs, expression, predicate, timeoutMs, intervalMs = 100) {
  const deadline = Date.now() + timeoutMs;
  let last;
  while (Date.now() < deadline) {
    last = await evalJs(expression);
    if (predicate(last)) return last;
    await new Promise((r) => setTimeout(r, intervalMs));
  }
  return last;
}

const SHELL_IDS = [
  "btn-new", "btn-new-scratch", "templates-select", "btn-open", "btn-save", "btn-save-as", "btn-close-page",
  "status-label", "tab-bar", "query-input", "solve-button", "solve10-button", "solve-all-button", "stop-button",
  "reset-button", "timeout-input", "editor", "side", "side-tab-bar",
];

const SCENARIOS = [
  {
    name: "app boots with the full static shell and no console errors",
    async run({ evalJs, exceptions }) {
      // The shell itself is static markup and always present immediately; wait for the Ace editor textarea
      // instead, since that only exists once WebIdeView has actually finished constructing and mounting.
      await pollUntil(evalJs, `!!document.querySelector('textarea.ace_text-input')`, (v) => v, 10_000);
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
        evalJs,
        `Array.from(document.querySelectorAll('.ace_line')).map(e => e.textContent).join('\\n')`,
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
        evalJs,
        `document.getElementById('status-label').textContent`,
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
