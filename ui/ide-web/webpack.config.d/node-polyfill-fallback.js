// ide-web only ever runs in a browser. Some transitive JVM/JS-multiplatform modules (written primarily for
// Node.js consumers, e.g. :repl) reference Node built-ins (fs/path/os) in code paths ide-web never calls;
// webpack 5 no longer polyfills these automatically, so tell it to treat them as absent instead of failing the build.
config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign({}, config.resolve.fallback, {
    fs: false,
    path: false,
    os: false,
});
