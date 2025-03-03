package com.receipts.receipt_sharing.data.helpers

object PasswordChecker {
    private var _minLength: Int = 15
    private var _lettersLeastCount: Int = 1
    private var _numbersLeastCount: Int = 1
    private var _hasSpecials: Boolean = true
    private var _hasUpperCase: Boolean = true
    private var _hasLowerCase: Boolean = true

    val MinLength: Int
        get() = _minLength
    val LettersLeastCount: Int
        get() = _lettersLeastCount
    val NumbersLeastCount: Int
        get() = _numbersLeastCount
    val HasSpecials: Boolean
        get() = _hasSpecials
    val HasUpperCase: Boolean
        get() = _hasUpperCase
    val HasLowerCase: Boolean
        get() = _hasLowerCase

    fun checkPassword(password: String): Boolean {
        return password.length >= _minLength
                && password.count { it.isDigit() } >= _numbersLeastCount
                && password.count { it.isLetter() } >= _lettersLeastCount
                && password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) == _hasSpecials
                && password.contains("[A-Z]".toRegex()) == _hasUpperCase
                && password.contains("[a-z]".toRegex()) == _hasLowerCase
    }

    fun define(minLength: Int = 15, lettersLeastCount: Int = 1, numbersLeastCount: Int = 1, hasSpecials: Boolean = true, hasUpperCase: Boolean = true, hasLowerCase: Boolean = true) {
        _minLength = minLength
        _lettersLeastCount = lettersLeastCount
        _numbersLeastCount = numbersLeastCount
        _hasSpecials = hasSpecials
        _hasUpperCase = hasUpperCase
        _hasLowerCase = hasLowerCase
    }
}