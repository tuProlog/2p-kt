package it.unibo.tuprolog.ide.web.redux.actions

import mui.material.AlertColor
import redux.RAction

class AddMessage(val text: String = "", val color: AlertColor = AlertColor.info) :
    RAction
class RemoveMessage(val id: String = "", val allFlag: Boolean = false) :
    RAction

enum class SortType {
    RECENT, SEVERITY
}
class SortByMessage(val sortType: SortType) :
    RAction

// TODO implement utility function from mui alert type to messageType
enum class MessageType {
    ERROR, WARNING, SUCCESS, INFO
}
class TypedMessage(val text: String = "", val type: MessageType = MessageType.INFO) :
    RAction