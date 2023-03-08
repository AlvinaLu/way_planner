package cz.cvut.fel.lushnalv.ui.daypoint

import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldState
import cz.cvut.fel.lushnalv.ui.theme.authorization.textFieldStateSaver

class NewDutyCurrencyCodeState :
    TextFieldState(validator = ::isCurrencyCodeValid, errorFor = ::newDutyCurrencyCodeValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun newDutyCurrencyCodeValidationError(code: String): String {
    return "Invalid amount: $code"
}

private fun isCurrencyCodeValid(code: String): Boolean {
    return true
}

val newDutyCurrencyCodeSaver = textFieldStateSaver(NewDutyCurrencyCodeState())