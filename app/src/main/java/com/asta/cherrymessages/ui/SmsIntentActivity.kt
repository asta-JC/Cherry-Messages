package com.asta.cherrymessages.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.asta.cherrymessages.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmsIntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recipient = intent.data?.schemeSpecificPart 
            ?: intent.getStringExtra("address") 
            ?: intent.getStringExtra("phone") 
            ?: ""

        val body = intent.getStringExtra("sms_body") 
            ?: intent.getStringExtra(Intent.EXTRA_TEXT) 
            ?: ""

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("extra_recipient", recipient)
            putExtra("extra_body", body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(mainIntent)
        finish()
    }
}
