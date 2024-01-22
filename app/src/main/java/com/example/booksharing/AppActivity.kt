package com.example.booksharing

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksharing.databinding.ActivityAppBinding
import com.example.booksharing.ui.BlankFragment
import com.example.booksharing.ui.account.AccountFragment
import com.example.booksharing.ui.borrowed.BorrowedFragment
import com.example.booksharing.ui.library.LibraryFragment
import com.example.booksharing.ui.notifications.NotificationFragment
import com.example.booksharing.ui.search.SearchFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppActivity : AppCompatActivity() {
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")
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
                R.id.nav_account, R.id.nav_library, R.id.nav_search,
                R.id.nav_blank, R.id.nav_notifications,R.id.nav_borrowed_books,
                R.id.nav_blank
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
                R.id.nav_blank ->{
                    navigateToBlankFragment()
                    drawerLayout.closeDrawers()
                    true
                }
                // Dodaj inne przypadki, jeśli są potrzebne
                else -> false
            }
        }

        checkNewExchanges()
    }

    private fun checkNewExchanges()= CoroutineScope(Dispatchers.IO).launch {
        try {
            var counter =0
            val querySnapshot = exchangeHistoryCollection.whereEqualTo("bookOwner",profileName)
                .whereEqualTo("state","oczekuje").get().await()
            for(document in querySnapshot){
                counter++
            }
            if(counter != 0){
                showNotification()
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@AppActivity,e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showNotification() {
        val context = this@AppActivity
        val channelId = "BookSharing_Channel"
        val channelName = "BookSharing Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = NotificationCompat.Builder(context, "BookSharing_Channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("BookSharing")
            .setContentText("Otrzymano nowe prośby o wymianę\n Sprawdź menu powiadomień!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    this@AppActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this@AppActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS),100)
                }
                return
            }
            notify(1, builder.build())
        }
    }

    private fun navigateToBlankFragment() {
        val fragment = BlankFragment()

        val bundle = Bundle()
        bundle.putString("username", profileName)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main,fragment).commit()
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