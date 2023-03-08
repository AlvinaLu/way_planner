package cz.cvut.fel.lushnalv.ui.daypoint

import cz.cvut.fel.lushnalv.ui.theme.authorization.TextFieldState
import cz.cvut.fel.lushnalv.ui.theme.authorization.textFieldStateSaver

class NewDutyTitleState :
    TextFieldState(validator = ::isNewDutyTitleValid, errorFor = ::newDutyTitleValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun newDutyTitleValidationError(title: String): String {
    return "Invalid title: $title"
}

private fun isNewDutyTitleValid(title: String): Boolean {
    return title.isNotEmpty() && title.length > 3
}

val NewDutyTitleSaver = textFieldStateSaver(NewDutyTitleState())

class NewCommentState :
    TextFieldState(validator = ::isNewCommentValid, errorFor = ::newCommentValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun newCommentValidationError(comment: String): String {
    return "Invalid note: $comment"
}

private fun isNewCommentValid(comment: String): Boolean {
    return comment.isNotEmpty()
}

val NewCommentSaver = textFieldStateSaver(NewCommentState())