package com.example.eloanmust.core.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for Google Sign-In operations
 */
sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
    data object Cancelled : GoogleSignInResult()
}

/**
 * Helper class for Google Sign-In with Firebase Authentication.
 * Handles the complete flow from Google Sign-In to Firebase ID Token retrieval.
 */
@Singleton
class GoogleAuthHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getWebClientId())
            .requestEmail()
            .build()
        
        GoogleSignIn.getClient(context, gso)
    }
    
    /**
     * Get Web Client ID from Firebase/Google Console
     * This should match your backend's expected client ID
     */
    private fun getWebClientId(): String {
        // Get from resources - you need to add this to strings.xml
        return context.getString(com.example.eloanmust.R.string.default_web_client_id)
    }
    
    /**
     * Get the Google Sign-In intent to launch the account picker
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    /**
     * Handle the result from Google Sign-In activity.
     * Signs in to Firebase and returns the Firebase ID Token.
     *
     * @param data The intent data from onActivityResult
     * @return GoogleSignInResult containing ID Token on success
     */
    suspend fun handleSignInResult(data: Intent?): GoogleSignInResult {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            if (account == null) {
                Timber.e("Google Sign-In failed: account is null")
                return GoogleSignInResult.Error("Gagal mendapatkan akun Google")
            }
            
            val googleIdToken = account.idToken
            if (googleIdToken == null) {
                Timber.e("Google Sign-In failed: idToken is null")
                return GoogleSignInResult.Error("Gagal mendapatkan token Google")
            }
            
            Timber.d("Google Sign-In successful, signing in to Firebase...")
            
            // Sign in to Firebase with Google credential
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            
            if (authResult.user == null) {
                Timber.e("Firebase Sign-In failed: user is null")
                return GoogleSignInResult.Error("Gagal masuk ke Firebase")
            }
            
            // Get Firebase ID Token to send to backend
            val firebaseIdToken = authResult.user!!.getIdToken(true).await()?.token
            
            if (firebaseIdToken == null) {
                Timber.e("Failed to get Firebase ID Token")
                return GoogleSignInResult.Error("Gagal mendapatkan token Firebase")
            }
            
            Timber.d("Firebase ID Token obtained successfully")
            GoogleSignInResult.Success(firebaseIdToken)
            
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In failed with status code: ${e.statusCode}")
            when (e.statusCode) {
                12501 -> GoogleSignInResult.Cancelled // User cancelled
                else -> GoogleSignInResult.Error("Login Google gagal: ${e.message}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In failed")
            GoogleSignInResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Sign out from Google and Firebase
     */
    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            Timber.d("Signed out from Google and Firebase")
        } catch (e: Exception) {
            Timber.e(e, "Error during sign out")
        }
    }
    
    /**
     * Revoke access (disconnect Google account)
     */
    suspend fun revokeAccess() {
        try {
            firebaseAuth.signOut()
            googleSignInClient.revokeAccess().await()
            Timber.d("Revoked Google access")
        } catch (e: Exception) {
            Timber.e(e, "Error during revoke access")
        }
    }
}
