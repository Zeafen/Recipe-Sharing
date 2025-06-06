package com.receipts.receipt_sharing.data.helpers

import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.presentation.ValidationInfo

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


    /**
     * Checks if password matches all defined rules
     * @param password password input
     * @return [ValidationInfo] with [ValidationInfo.isValid] equals to true if password is valid; otherwise - false
     * @see [ValidationInfo]
     */
    fun checkPassword(password: String): ValidationInfo {
        return when {
            password.length < _minLength -> ValidationInfo(
                false, R.string.incorrect_length_least_error, listOf(
                    _minLength
                )
            )

            password.count { it.isDigit() } < _numbersLeastCount -> ValidationInfo(
                false, R.string.must_contain_least_numbers_error, listOf(
                    _numbersLeastCount
                )
            )

            password.count { it.isLetter() } < _lettersLeastCount -> ValidationInfo(
                false, R.string.must_contain_least_letters_error, listOf(
                    _lettersLeastCount
                )
            )

            !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) == _hasSpecials -> ValidationInfo(
                false, R.string.must_contain_specials_error
            )

            !password.contains("[A-Z]".toRegex()) == _hasUpperCase -> ValidationInfo(
                false, R.string.must_contain_uppercase,
            )

            !password.contains("[a-z]".toRegex()) == _hasLowerCase -> ValidationInfo(
                false, R.string.must_contain_lowercase
            )

            else -> ValidationInfo(true)
        }


    }

    /**
     * Defines rules for checking password
     * @param minLength password min length
     * @param hasSpecials should password contain specials
     * @param hasLowerCase should password contain lowercase letters
     * @param hasUpperCase password contain uppercase letters
     * @param lettersLeastCount min number of letters password must contain
     * @param numbersLeastCount min number of numbers password must contain
     */
    fun define(
        minLength: Int = 15,
        lettersLeastCount: Int = 1,
        numbersLeastCount: Int = 1,
        hasSpecials: Boolean = true,
        hasUpperCase: Boolean = true,
        hasLowerCase: Boolean = true
    ) {
        _minLength = minLength
        _lettersLeastCount = lettersLeastCount
        _numbersLeastCount = numbersLeastCount
        _hasSpecials = hasSpecials
        _hasUpperCase = hasUpperCase
        _hasLowerCase = hasLowerCase
    }
}