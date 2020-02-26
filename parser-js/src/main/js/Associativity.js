const Associativity = Object.freeze({
    XF: "XF",
    YF: "YF",
    XFX: "XFX",
    XFY: "XFY",
    YFX: "YFX",
    FX: "FX",
    FY: "FY",
    PREFIX: ["FX", "FY"],
    NON_PREFIX: ["XF","YF","XFY","YFX","XFX"],
    INFIX: ["XFX","XFY","YFX"],
    POSTFIX: ["XF","YF"]
});


exports.Associativity = Associativity;