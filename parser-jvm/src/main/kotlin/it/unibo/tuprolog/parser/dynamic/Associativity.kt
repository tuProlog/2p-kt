package it.unibo.tuprolog.parser.dynamic

import java.util.EnumSet
import java.util.stream.Stream

enum class Associativity {
    XF, YF, XFX, XFY, YFX, FX, FY;

    companion object {

        @JvmStatic
        fun values(i: Int): Associativity {
            return values()[i]
        }

        @JvmField
        val X_FIRST = EnumSet.of(XF, XFX, XFY, FX)

        @JvmField
        val Y_FIRST = EnumSet.of(YF, YFX, FY)

        @JvmField
        val INFIX = EnumSet.of(XFX, YFX, XFY)

        @JvmField
        val PREFIX = EnumSet.of(FX, FY)

        @JvmField
        val POSTFIX = EnumSet.of(YF, XF)

        @JvmField
        var NON_PREFIX = EnumSet.complementOf(PREFIX)

        @JvmStatic
        fun isAssociativity(value: String?): Boolean {
            return Stream.of(*values())
                .anyMatch {
                    it.toString().equals(value, ignoreCase = true)
                }
        }
    }
}
