package com.atech.bit.ui.screens.login.util

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.atech.bit.BuildConfig
import com.atech.core.utils.TAGS
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val oneTapClient: SignInClient
) {
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e else null
        }
        return result?.pendingIntent?.intentSender
    }


    fun signInWithIntent(data: Intent): Pair<String?, Exception?> = try {
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val googleIdToken = credential.googleIdToken
        googleIdToken?.let {
            return it to null
        }
        Pair(null, Exception("No Google ID token"))
    } catch (e: Exception) {
        Log.d("AAA", "signInWithIntent: $e")
        Pair(null, e)
    }

    suspend fun signOut(
        action: () -> Unit
    ) {
        try {
            oneTapClient.signOut().await()
            action()
        } catch (e: Exception) {
            Log.e(TAGS.BIT_ERROR.name, "signOut: $e")
        }
    }


    private fun buildSignInRequest(): BeginSignInRequest {
        val webClient = BuildConfig.FIREBASE_WEB_CLIENT
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.Builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false).setServerClientId(webClient).build()
        ).setAutoSelectEnabled(true).build()
    }
}