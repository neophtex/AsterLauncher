package com.series.aster.launcher.helper

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.fragment.app.Fragment
import com.series.aster.launcher.R
import com.series.aster.launcher.data.entities.AppInfo
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FingerprintHelper @Inject constructor(private val fragment: Fragment) {

    private lateinit var callback: Callback

    interface Callback {
        fun onAuthenticationSucceeded(appInfo: AppInfo)
        fun onAuthenticationFailed()
        fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?)
    }

    fun startFingerprintAuth(appInfo: AppInfo, callback: Callback) {
        this.callback = callback

        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callback.onAuthenticationSucceeded(appInfo)
            }

            override fun onAuthenticationFailed() {
                callback.onAuthenticationFailed()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
                callback.onAuthenticationError(errorCode, errorMessage)
            }
        }

        val executor = ContextCompat.getMainExecutor(fragment.requireContext())
        val biometricPrompt = BiometricPrompt(fragment, executor, authenticationCallback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(fragment.getString(R.string.authentication_title))
            .setNegativeButtonText(fragment.getString(R.string.authentication_cancel))
            .build()

        val canAuthenticate = BiometricManager.from(fragment.requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}
