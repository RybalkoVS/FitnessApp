package com.example.fitnessapp.presentation.splash

import android.animation.AnimatorInflater
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

    private lateinit var appLogo: ImageView
    private val preferencesRepository = DependencyProvider.preferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        appLogo = findViewById(R.id.app_logo)
        startAnimation()
    }

    private fun startAnimation() {
        AnimatorInflater.loadAnimator(this, R.animator.anim_rotate)
            .apply {
                setTarget(appLogo)
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
        val token = preferencesRepository.getAuthorizationToken(context = this)
        return !token.isNullOrEmpty()
    }
}