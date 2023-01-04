package com.example.worldmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.worldmemo.broadcast.NetworkReceiver
import com.example.worldmemo.databinding.ActivityMainBinding
import com.example.worldmemo.ui.add.AddFragmentDirections
import com.example.worldmemo.utils.NetworkUtils
import com.example.worldmemo.utils.PermissionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ImageLoaderFactory {

    private lateinit var binding: ActivityMainBinding
    private val networkReceiver = NetworkReceiver()

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_country
            )
        )



        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        requestMultiplePermissions.launch(PermissionUtils(this).permissions)

        // Redirect the user in case an intent is received on the app opening
        val intent = intent
        if (intent.type?.contains("audio/") == true) {
            navView.selectedItemId = R.id.navigation_add
            val action = AddFragmentDirections.actionNavigationAddToAddAudioFragment(null)
            navController.navigate(action)
        } else if (intent.type?.contains("image/") == true) {
            navView.selectedItemId = R.id.navigation_add
            val action = AddFragmentDirections.actionNavigationAddToAddPhotoFragment(null)
            navController.navigate(action)
        }

        // handle broadcast receiver
        networkReceiver.intentFilter.forEach {
            this.registerReceiver(networkReceiver, it)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_no_internet -> {
                startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.airplane, menu)

        val noInternetMenuItem: MenuItem = menu!!.findItem(R.id.action_no_internet)

        this.networkReceiver.addMenuItem(noInternetMenuItem)

        val isConnected: Boolean = if (NetworkUtils.isAirplaneModeOn(this)) {
            false
        } else NetworkUtils.isInternetAvailable(this)

        if (!isConnected) {
            noInternetMenuItem.isVisible = true
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).memoryCache {
            MemoryCache.Builder(this).maxSizePercent(0.25).build()
        }.diskCache {
            DiskCache.Builder().directory(this.cacheDir.resolve("image_cache")).maxSizePercent(0.02)
                .build()
        }.build()
    }
}