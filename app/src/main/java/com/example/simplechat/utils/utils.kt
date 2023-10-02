package com.example.simplechat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

public class Utils {
    /**
     * showToast function for displaying short-duration toast messages.
     *
     * This function simplifies the process of displaying toast messages in the app by taking
     * the message text and the application context as parameters.
     *
     * @param message The message text to be displayed in the toast.
     * @param context The application context in which the toast should appear.
     */
    fun  showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * moveTo function for navigating between activities.
     *
     * This function simplifies the process of starting a new activity by taking the current
     * context and the target activity class as parameters.
     *
     * @param from The current context from which the navigation is initiated.
     * @param to The target activity class to which navigation should occur.
     */
    fun moveTo(from: Context, to: Class<out Activity>) {
        val intent = Intent(from, to)
        from.startActivity(intent)
    }
}