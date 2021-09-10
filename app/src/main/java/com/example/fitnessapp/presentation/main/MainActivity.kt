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
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.main.notification.NotificationsFragment
import com.example.fitnessapp.presentation.main.track.TrackFragment
import com.example.fitnessapp.presentation.main.track.TrackListFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), FragmentContainerActivityCallback {

    companion object {
        private const val CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
        private const val MIN_BACK_STACK_SIZE = 1
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var logoutBtn: Button
    private lateinit var navigationView: NavigationView
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
            savedInstanceState.getString(CURRENT_FRAGMENT)?.let {
                showFragment(it)
            }
        } else {
            showFragment(TrackListFragment.TAG)
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
                    showFragment(TrackListFragment.TAG)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.menu_item_notifications -> {
                    showFragment(NotificationsFragment.TAG)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    false
                }
            }
        }

    override fun showFragment(fragmentTag: String, args: Bundle?) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment != null) {
            supportFragmentManager.popBackStack(fragmentTag, 0)
        } else {
            fragment = getFragmentByTag(fragmentTag, args)
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container_main,
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
            NotificationsFragment.TAG -> {
                NotificationsFragment.newInstance()
            }
            TrackFragment.TAG -> {
                TrackFragment.newInstance(args)
            }
            else -> {
                TrackListFragment.newInstance()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.findFragmentById(R.id.fragment_container_main)?.let {
            outState.putString(CURRENT_FRAGMENT, it.tag)
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
        if (supportFragmentManager.backStackEntryCount == MIN_BACK_STACK_SIZE) {
            finish()
        }
        super.onBackPressed()
    }

    override fun closeActivity() {
        finish()
    }
}