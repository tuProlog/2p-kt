package it.unibo.tuprolog.parser.dynamic

import java.util.EnumSet

enum class Associativity {
    XF,
    YF,
    XFX,
    XFY,
    YFX,
    FX,
    FY,
    ;

    companion object {
        @JvmStatic
        fun values(i: Int): Associativity = values()[i]

        @JvmField
        val X_FIRST: EnumSet<Associativity> = EnumSet.of(XF, XFX, XFY, FX)

        @JvmField
        val Y_FIRST: EnumSet<Associativity> = EnumSet.of(YF, YFX, FY)

        @JvmField
        val INFIX: EnumSet<Associativity> = EnumSet.of(XFX, YFX, XFY)

        @JvmField
        val PREFIX: EnumSet<Associativity> = EnumSet.of(FX, FY)

        @JvmField
        val POSTFIX: EnumSet<Associativity> = EnumSet.of(YF, XF)

        @JvmField
        val NON_PREFIX: EnumSet<Associativity> = EnumSet.complementOf(PREFIX)

        @JvmStatic
        fun isAssociativity(value: String?): Boolean =
            values().asSequence().any {
                it.name.equals(value, ignoreCase = true)
            }
    }
}
