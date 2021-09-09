package com.example.fitnessapp.presentation.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.FragmentContainerActivity

class AuthorizationActivity : AppCompatActivity(), FragmentContainerActivity {

    companion object {
        const val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
        const val MIN_BACK_STACK_SIZE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        if (savedInstanceState != null) {
            savedInstanceState.getString(CURRENT_FRAGMENT)?.let {
                showFragment(it)
            }
        } else {
            showFragment(LoginFragment.TAG)
        }
    }

    override fun showFragment(fragmentTag: String, args: Bundle?) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment != null) {
            supportFragmentManager.popBackStack(fragmentTag, 0)
        } else {
            fragment = getFragmentByTag(fragmentTag)
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container_authorization,
                    fragment,
                    fragmentTag
                )
                addToBackStack(fragment.tag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                commit()
            }
        }
    }

    override fun getFragmentByTag(fragmentTag: String, args: Bundle?): Fragment {
        return when (fragmentTag) {
            RegisterFragment.TAG -> {
                RegisterFragment.newInstance()
            }
            else -> {
                LoginFragment.newInstance()
            }
        }
    }

    override fun closeActivity() {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.findFragmentById(R.id.fragment_container_authorization)?.let {
            outState.putString(CURRENT_FRAGMENT, it.tag)
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