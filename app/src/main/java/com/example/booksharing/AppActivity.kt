package com.example.booksharing

import android.app.FragmentTransaction
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksharing.R
import com.example.booksharing.databinding.ActivityAppBinding
import com.example.booksharing.ui.account.AccountFragment
import com.example.booksharing.ui.library.LibraryFragment
import com.example.booksharing.ui.search.SearchFragment
import com.google.android.material.navigation.NavigationView

class AppActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAppBinding
    private var profileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarApp.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        profileName = intent.getStringExtra("username")
        if (profileName.isNullOrEmpty()) {
            profileName = "DefaultUsername"
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_account, R.id.nav_library, R.id.nav_search, R.id.nav_blank
            ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    navigateToAccountFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_search -> {
                    navigateToSearchFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_library -> {
                    navigateToLibraryFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                // Dodaj inne przypadki, jeśli są potrzebne
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun navigateToAccountFragment() {
        val fragment = AccountFragment()

        val bundle = Bundle()
        bundle.putString("profileName", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()
    }
    private fun navigateToLibraryFragment(){
        val fragment = LibraryFragment()

        val bundle = Bundle()
        bundle.putString("profileName", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
    }
    private fun navigateToSearchFragment(){
        val fragment = SearchFragment()

        val bundle = Bundle()
        bundle.putString("profileName", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
    }

}