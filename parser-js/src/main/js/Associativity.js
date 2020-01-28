const Associativity = Object.freeze({
    XF: 0,
    YF: 1,
    XFX: 2,
    XFY: 3,
    YFX: 4,
    FX: 5,
    FY: 6,
    PREFIX: [this.FX, this.FY],
    NON_PREFIX: [this.XF,this.YF,this.XFY,this.YFX,this.XFX],
    INFIX: [this.XFX,this.XFY,this.YFX],
    POSTFIX: [this.XF,this.YF]
});


exports.Associativity = Associativity