package service

import android.content.Context
import android.widget.Toast
object Toast {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}