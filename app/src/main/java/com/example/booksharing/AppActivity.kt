package com.example.booksharing

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksharing.databinding.ActivityAppBinding
import com.example.booksharing.ui.account.AccountFragment
import com.example.booksharing.ui.borrowed.BorrowedFragment
import com.example.booksharing.ui.library.LibraryFragment
import com.example.booksharing.ui.notifications.NotificationFragment
import com.example.booksharing.ui.search.SearchFragment
import com.google.android.material.navigation.NavigationView

class AppActivity : AppCompatActivity() {
    companion object {
        const val ACTION_SEND_NOTIFICATION = "com.example.booksharing.broadcastreceivers.ACTION_SEND_NOTIFICATION"
    }
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
                R.id.nav_account, R.id.nav_library, R.id.nav_search, R.id.nav_blank, R.id.nav_notifications,R.id.nav_borrowed_books
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
                R.id.nav_notifications -> {
                    navigateToNotificationsFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_borrowed_books -> {
                    navigateToBorrowedFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                // Dodaj inne przypadki, jeśli są potrzebne
                else -> false
            }
        }
    }

    private fun navigateToBorrowedFragment() {
        val fragment = BorrowedFragment()

        val bundle = Bundle()
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
    }

    private fun navigateToNotificationsFragment() {
        val fragment = NotificationFragment()

        val bundle = Bundle()
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
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
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()
    }
    private fun navigateToLibraryFragment(){
        val fragment = LibraryFragment()

        val bundle = Bundle()
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
    }
    private fun navigateToSearchFragment(){
        val fragment = SearchFragment()

        val bundle = Bundle()
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
    }

}