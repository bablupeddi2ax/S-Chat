package com.example.simplechat.utils

/**
 * `ValidationResult` is a sealed class used for representing the outcomes of validation processes.
 * It provides two subclasses: [Success] and [Error], each indicating a different validation result.
 *
 * @see Success
 * @see Error
 */
sealed class ValidationResult {

    /**
     * Represents a successful validation result.
     *
     * @property data The data associated with the successful validation.
     */
    data class Success(val data: String) : ValidationResult()

    /**
     * Represents an error validation result.
     *
     * @property message The error message describing the validation failure.
     */
    data class Error(val message: String) : ValidationResult()
}

