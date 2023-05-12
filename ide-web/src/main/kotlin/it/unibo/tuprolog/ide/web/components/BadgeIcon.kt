package it.unibo.tuprolog.ide.web.components

import mui.icons.material.SvgIconComponent
import mui.material.Badge
import mui.material.BadgeColor
import react.FC
import react.Props
import react.ReactNode

external interface NumberedIconProps : Props {
    var number: Int
    var icon: SvgIconComponent
}

val NumberedIcon = FC<NumberedIconProps> { props ->

    Badge {
        color = BadgeColor.default
        badgeContent = ReactNode(props.number.toString())

        props.icon {
            // TODO add icon props mapping to IconWithNumberProps
        }
    }
}