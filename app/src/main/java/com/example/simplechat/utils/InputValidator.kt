package com.example.simplechat.utils

//regex are used instead of if else statements

// email validation through regex
fun validateEmail(email: String): ValidationResult {

    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return if (email.matches(emailRegex)) {
        ValidationResult.Success(email)
    } else {
        ValidationResult.Error("Invalid email")
    }
}

// password validation through regex
//methods return ValidationResult.Success(password) if it matches with regex else error message
/* [TODO] //add particular elements that
     are missing in password like what is missing
     in password specifically like capital letter
     or . or @ so that user can understand exactly what he has to eneter

 */

// password validation through regex
fun validatePassword(password: String): ValidationResult {
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()
    return if (password.matches(passwordRegex)) {
        ValidationResult.Success(password)
    } else {
        ValidationResult.Error("Invalid password")
    }
}



/*
Start with ^: The email must start at the beginning of the string.

Username:

It can contain letters (both uppercase and lowercase), numbers, dots, underscores, percent signs, plus signs, and hyphens.
It must have at least one character (no empty usernames).
@ Symbol: There must be an "@" symbol, which separates the username from the domain.

Domain Name:

Similar to the username, it can contain letters (uppercase and lowercase), numbers, dots, hyphens, and at least one character.
It's what comes after the "@" symbol.
Dot (.) Symbol: There must be a literal dot (.) after the domain name.

Top-Level Domain (TLD):

It consists of letters (uppercase and lowercase) only.
It must be between 2 and 6 characters long.
It represents things like "com," "org," "net," etc.
End with $: The email must end at this point; there shouldn't be anything else after the TLD.
 */



/*
Start with ^: The password validation must start at the beginning of the string.

(?=.*[0-9]):

There must be at least one digit (0-9) in the password.
This part ensures the password contains a number.
(?=.*[a-zA-Z]):

There must be at least one letter (a-z or A-Z) in the password.
This part ensures the password contains a letter.
(?=.*[@#$%^&+=]):

There must be at least one special character among @, #, $, %, ^, &, +, or = in the password.
This part ensures the password contains a special character.
(?=\S+$):

The entire password must not contain any whitespace (spaces or tabs).
This ensures there are no spaces in the password.
.{8,}:

The password must be at least 8 characters long.
This part enforces a minimum length for the password.
End with $: The password validation must end at this point; there shouldn't be anything else after the specified conditions.
 */