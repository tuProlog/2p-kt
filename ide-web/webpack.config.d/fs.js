// check if only fs is required

config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
        os: false,
    }
};