package cz.cvut.fel.lushnalv.ui.createNewTrip

import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldState
import cz.cvut.fel.lushnalv.ui.theme.authorization.textFieldStateSaver
import java.util.regex.Pattern

class TitleNewTripState:
    TextFieldState(validator = ::isTitleNewTripStateValid, errorFor = ::titleNewTripStateValidationError)
/**
 * Returns an error to be displayed or null if no error was found
 */
private fun titleNewTripStateValidationError(title: String): String {
    return "Invalid title: $title"
}

private fun isTitleNewTripStateValid(title: String): Boolean {
    return title.length > 3
}

val TitleNewTripStateSaver = textFieldStateSaver(TitleNewTripState())