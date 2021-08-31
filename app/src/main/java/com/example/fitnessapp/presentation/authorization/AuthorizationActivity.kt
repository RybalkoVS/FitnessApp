package com.example.fitnessapp.presentation.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessapp.R

class AuthorizationActivity : AppCompatActivity(), AuthorizationActivityCallback {

    companion object {
        const val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
        const val SAVED_DATA = "SAVED_DATA"
        const val MIN_BACK_STACK_SIZE = 1
    }

    private var currentFragmentTag: String? = null
    private var enteredData: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        if (savedInstanceState != null) {
            enteredData = savedInstanceState.getBundle(SAVED_DATA)
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT)
            currentFragmentTag?.let {
                restoreFragmentState(it)
            } ?: moveToLoginFragment()
        } else {
            moveToLoginFragment()
        }
    }

    private fun restoreFragmentState(tag: String) {
        if (tag == RegisterFragment.TAG) {
            moveToRegisterFragment()
        } else {
            moveToLoginFragment()
        }
    }

    override fun moveToLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_authorization,
                LoginFragment.newInstance(enteredData),
                LoginFragment.TAG
            )
            .addToBackStack(LoginFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun moveToRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_authorization,
                RegisterFragment.newInstance(enteredData),
                RegisterFragment.TAG
            )
            .addToBackStack(RegisterFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun saveEnteredData(data: Bundle?) {
        enteredData = data
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_authorization)
        if (currentFragment != null) {
            outState.putString(CURRENT_FRAGMENT, currentFragment.tag)
            outState.putBundle(SAVED_DATA, enteredData)
        }
    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            MIN_BACK_STACK_SIZE -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}