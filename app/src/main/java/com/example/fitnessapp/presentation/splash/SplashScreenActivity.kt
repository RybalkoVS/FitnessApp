package com.example.fitnessapp.presentation.splash

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity

class SplashScreenActivity : AppCompatActivity() {

    companion object{
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"
    }

    private var appLogo: ImageView? = null
    private var rotationAnim: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        appLogo = findViewById(R.id.app_logo)
        rotationAnim = AnimatorInflater.loadAnimator(this, R.animator.anim_rotation) as ObjectAnimator
        rotationAnim?.apply {
            target = appLogo
            start()
            doOnEnd {
                moveToNextScreen()
                finish()
            }
        }
    }

    private fun isUserAuthorized(): Boolean {
        val token = getAuthorizationToken()
        return !token.isNullOrEmpty()
    }

    private fun moveToNextScreen() {
        if (isUserAuthorized()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAuthorizationToken(): String? {
        val preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, "")
    }

}