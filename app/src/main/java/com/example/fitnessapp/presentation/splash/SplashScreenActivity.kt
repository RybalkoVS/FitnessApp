package com.example.fitnessapp.presentation.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val ROTATE_ANIM_DURATION = 3000L
        const val ROTATE_ANIM_DEGREE = 360f
    }

    private lateinit var appLogo: ImageView
    private val preferencesStore = DependencyProvider.preferencesStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        appLogo = findViewById(R.id.app_logo)
        ObjectAnimator.ofFloat(appLogo, "rotation", ROTATE_ANIM_DEGREE).apply {
            duration = ROTATE_ANIM_DURATION
            start()
            doOnEnd {
                moveToNextScreen()
                finish()
            }
        }
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

    private fun isUserAuthorized(): Boolean {
        val token = preferencesStore.getAuthorizationToken(context = this)
        return !token.isNullOrEmpty()
    }
}