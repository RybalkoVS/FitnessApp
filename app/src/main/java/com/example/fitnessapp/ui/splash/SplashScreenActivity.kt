package com.example.fitnessapp.ui.splash

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.authorization.AuthorizationActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var appLogo: ImageView
    private lateinit var rotationAnim: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        appLogo = findViewById(R.id.app_logo)
        rotationAnim = AnimatorInflater.loadAnimator(this, R.animator.anim_rotation) as ObjectAnimator
        with(rotationAnim) {
            target = appLogo
            start()
            doOnEnd {
                moveToNextScreen()
                finish()
            }
        }
    }

    private fun isUserAuthorized(): Boolean {
        val token = ""
        return token.isNotEmpty()
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

}