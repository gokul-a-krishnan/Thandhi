package com.gstudios.thandhi.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gstudios.thandhi.util.FirestoreUtil

class FirebaseMsgService : FirebaseMessagingService() {

//    TODO 10:19

    override fun onNewToken(token: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            addTokenToFirestore(token)
        }

    }

    companion object {
        fun addTokenToFirestore(newRegToken: String?) {
            if (newRegToken == null) throw NullPointerException("addTokenToFirestore token is Null")
            FirestoreUtil.getFCMRegTokens { tokens ->
                if (tokens.contains(newRegToken)) return@getFCMRegTokens
                tokens.add(newRegToken)
                FirestoreUtil.setFCMRegTokens(tokens)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            //TODO Show Notification
            Log.d("FCM Notifiy", remoteMessage.data.toString())
        }
    }

}