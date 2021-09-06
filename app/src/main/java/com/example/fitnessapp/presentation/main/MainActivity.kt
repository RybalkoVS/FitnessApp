package com.example.fitnessapp.presentation.main

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.notification.NotificationsFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        const val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
        const val MIN_BACK_STACK_SIZE = 1
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var logoutBtn: Button
    private lateinit var navigationView: NavigationView
    private var currentFragmentTag: String? = null
    private val preferencesStore = FitnessApp.INSTANCE.preferencesStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setSupportActionBar(toolbar)
        configureDrawerLayout()

        logoutBtn.setOnClickListener {
            onLogout()
        }

        navigationView.setNavigationItemSelectedListener(setupNavigationListener())

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT)
            currentFragmentTag?.let {
                restoreFragmentState(it)
            }
        } else {
            showFragment(MainFragment.TAG, MainFragment.newInstance())
        }
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        logoutBtn = findViewById(R.id.btn_logout)
        navigationView = findViewById(R.id.navigation_view)
    }

    private fun configureDrawerLayout() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
    }

    private fun onLogout() {
        preferencesStore.clearAuthorizationToken()
        moveToAuthorization()
    }

    private fun moveToAuthorization() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupNavigationListener() =
        NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_main -> {
                    supportFragmentManager.popBackStack(MainFragment.TAG, 0)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.menu_item_notifications -> {
                    showFragment(NotificationsFragment.TAG, NotificationsFragment.newInstance())
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    false
                }
            }
        }

    private fun restoreFragmentState(fragmentTag: String) {
        when (fragmentTag) {
            MainFragment.TAG -> {
                showFragment(MainFragment.TAG, MainFragment.newInstance())
            }
            NotificationsFragment.TAG -> {
                showFragment(NotificationsFragment.TAG, NotificationsFragment.newInstance())
            }
        }
    }

    private fun showFragment(fragmentTag: String, fragment: Fragment) {
        supportFragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_container_main,
                fragment,
                fragmentTag
            )
            addToBackStack(fragmentTag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return
        }
        if(supportFragmentManager.backStackEntryCount == MIN_BACK_STACK_SIZE){
            finish()
        }
        super.onBackPressed()
    }
}