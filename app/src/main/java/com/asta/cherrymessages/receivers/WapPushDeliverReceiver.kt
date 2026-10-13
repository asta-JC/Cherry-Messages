package com.asta.cherrymessages.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WapPushDeliverReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "WapPushDeliverReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION) {
            Log.d(TAG, "Received WAP_PUSH_DELIVER (MMS notification)")
            // Handle WAP push / MMS notification if needed
        }
    }
}
