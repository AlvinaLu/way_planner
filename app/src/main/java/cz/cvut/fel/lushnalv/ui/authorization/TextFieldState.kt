package cz.cvut.fel.lushnalv.ui.theme.authorization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import java.util.regex.Pattern

open class TextFieldState(
    private val validator: (String) -> Boolean = { true },
    private val errorFor: (String) -> String = { "" }
) {
    var text: String by mutableStateOf("")
    var isFocusedDirty: Boolean by mutableStateOf(false)
    var isFocused: Boolean by mutableStateOf(false)
    private var displayErrors: Boolean by mutableStateOf(false)

    open val isValid: Boolean
        get() = validator(text)

    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if (focused) isFocusedDirty = true
    }

    fun enableShowErrors() {
        if (isFocusedDirty) {
            displayErrors = true
        }
    }

    fun showErrors() = !isValid && displayErrors

    open fun getError(): String? {
        return if (showErrors()) {
            errorFor(text)
        } else {
            null
        }
    }
}

fun textFieldStateSaver(state: TextFieldState) = listSaver<TextFieldState, Any>(
    save = { listOf(it.text, it.isFocusedDirty) },
    restore = {
        state.apply {
            text = it[0] as String
            isFocusedDirty = it[1] as Boolean
        }
    }
)

private const val EMAIL_VALIDATION_REGEX =
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"

private const val CODE_VALIDATION_REGEX = "^\\d{4}\$"

class EmailState :
    TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun emailValidationError(email: String): String {
    return "Invalid email: $email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}

val EmailStateSaver = textFieldStateSaver(EmailState())

class CodeState :
    TextFieldState(validator = ::isCodeValid, errorFor = ::codeValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun codeValidationError(code: String): String {
    return "Invalid code format: $code"
}

private fun isCodeValid(code: String): Boolean {
    return Pattern.matches(CODE_VALIDATION_REGEX, code)
}

val CodeStateSaver = textFieldStateSaver(EmailState())



class NameState :
    TextFieldState(validator = ::isNameValid, errorFor = ::nameValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun nameValidationError(name: String): String {
    return "The $name must be at least 4 characters"
}

private fun isNameValid(name: String): Boolean {
    return name.length > 3
}

val NameStateSaver = textFieldStateSaver(EmailState())

class PasswordState :
    TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)

private fun isPasswordValid(password: String): Boolean {
    return password.length > 8 && (password.firstOrNull { it.isDigit() } != null) && (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } != null) && (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } != null)
}

@Suppress("UNUSED_PARAMETER")
private fun passwordValidationError(password: String): String {
    return "The password must be at least 8 characters and contain at least 1 lowercase, 1 uppercase, and 1 number"
}

class RepeatPasswordState(function: (String) -> Boolean) :
    TextFieldState(validator = function, errorFor = ::passwordRepeatValidationError)


@Suppress("UNUSED_PARAMETER")
private fun passwordRepeatValidationError(password: String): String {
    return "Passwords don't math"
}


