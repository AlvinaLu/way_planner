package cz.cvut.fel.lushnalv.ui.daypoint

import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldState
import cz.cvut.fel.lushnalv.ui.theme.authorization.textFieldStateSaver

class NewDutyAmountState :
    TextFieldState(validator = ::isNewDutyAmountValid, errorFor = ::newDutyAmountValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun newDutyAmountValidationError(amount: String): String {
    return "Invalid amount: $amount"
}

private fun isNewDutyAmountValid(amount: String): Boolean {
    return (!amount.startsWith("0") && amount.isNotEmpty())
}

val NewDutyAmountSaver = textFieldStateSaver(NewDutyAmountState())