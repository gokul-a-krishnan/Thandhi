package com.gstudios.thandhi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.iid.FirebaseInstanceId
import com.gstudios.thandhi.service.FirebaseMsgService
import com.gstudios.thandhi.util.FirestoreUtil
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SigninActivity : AppCompatActivity() {

    private val rcSignIn = 1
    private val signinProviders = listOf(
        AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .setRequireName(true)
            .build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signinProviders)
                .setLogo(R.drawable.ic_thandhi)
                .build()
            startActivityForResult(intent, rcSignIn)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                //Todo Initialize User in Firestore
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@addOnCompleteListener
                        }
                        val token = task.result!!.token
                        FirebaseMsgService.addTokenToFirestore(token)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return
                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        layout_sign_in.longSnackbar("No Network")
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        layout_sign_in.longSnackbar("Unknown Error")
                    }
                }
            }
        }
    }
}
