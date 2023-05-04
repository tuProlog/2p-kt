package it.unibo.tuprolog.ide.web.components

import csstype.Color
import mui.icons.material.Mail
import mui.material.Badge
import mui.material.BadgeColor
import react.FC
import react.Props
import react.ReactNode



val BadgeIcon = FC<Props> {

    Badge {
        color = BadgeColor.default
        badgeContent = ReactNode("3")

        Mail()
    }
}