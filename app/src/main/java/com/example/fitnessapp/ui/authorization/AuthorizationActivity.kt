package com.example.fitnessapp.ui.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessapp.R

class AuthorizationActivity : AppCompatActivity(), AuthorizationActivityCallback {

    companion object {
        const val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
        const val SAVED_DATA = "SAVED_DATA"
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != null) {
            outState.putString(CURRENT_FRAGMENT, currentFragment.tag)
            outState.putBundle(SAVED_DATA, enteredData)
        }
    }

    override fun moveToLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment.newInstance(enteredData), LoginFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun moveToRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegisterFragment.newInstance(enteredData), RegisterFragment.TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun saveEnteredData(data: Bundle?) {
        enteredData = data
    }

    private fun restoreFragmentState(tag: String) {
        if (tag == RegisterFragment.TAG) {
            moveToRegisterFragment()
        } else {
            moveToLoginFragment()
        }
    }

}