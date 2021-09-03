package com.example.fitnessapp.presentation.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
            }
        } else {
            showFragment(LoginFragment.newInstance(null), LoginFragment.TAG)
        }
    }

    private fun restoreFragmentState(tag: String) {
        when (tag) {
            RegisterFragment.TAG -> {
                showFragment(RegisterFragment.newInstance(enteredData), tag)
            }
            else -> {
                showFragment(LoginFragment.newInstance(enteredData), tag)
            }
        }
    }

    override fun showFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_container_authorization,
                fragment,
                fragmentTag
            )
            addToBackStack(fragmentTag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }
    }

    override fun closeActivity() {
        finish()
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