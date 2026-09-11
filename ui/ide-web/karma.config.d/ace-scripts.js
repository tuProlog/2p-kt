// Ace's own module system expects a plain global script load (see ace/Ace.kt), which webpack/Karma's bundling
// does not provide by default; load its two prebuilt files directly from where yarn installed the npm package
// so tests that construct a real Ace instance (AceEditorViewTest) see a working `window.ace` global.
// config.basePath here is .../build/js/packages/2p-ide-web-test; the shared node_modules lives two levels up.
var path = require("path");
var aceDir = path.resolve(config.basePath, "../../node_modules/ace-builds/src-min-noconflict");

config.files.unshift(
    { pattern: path.join(aceDir, "ace.js"), included: true, served: true, watched: false },
    { pattern: path.join(aceDir, "theme-github.js"), included: true, served: true, watched: false },
);
