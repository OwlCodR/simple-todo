package com.example.simpletodo

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.STATE_DRAGGING
import androidx.drawerlayout.widget.DrawerLayout.STATE_SETTLING
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.simpletodo.databinding.ActivityMainBinding
import com.example.simpletodo.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var appBarMain: AppBarMainBinding
    private val scaleFactor: Float = 6f
    private val animationDuration: Long = 250 // ms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TodoApp", "onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawer = binding.drawerLayout
        drawer.setScrimColor(Color.TRANSPARENT)
        drawer.drawerElevation = 0f

        appBarMain = binding.appBarMain

        setSupportActionBar(appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navView = binding.navView

        appBarConfiguration = AppBarConfiguration(navController.graph, drawer)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        drawer.addDrawerListener(object : ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)

                if (newState == STATE_SETTLING || newState == STATE_DRAGGING) {
                    val isOpened = drawer.isDrawerOpen(GravityCompat.START)
                    changeBackgroundColor(isOpened)
                }
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Log.d("TodoApp", "onDrawerSlide $slideOffset")

                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                val layout: CardView = appBarMain.root

                layout.translationX = slideX
                layout.scaleX = 1 - (slideOffset / scaleFactor)
                layout.scaleY = 1 - (slideOffset / scaleFactor)
            }
        })
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        drawer.closeDrawer(GravityCompat.START, false)
    }

    private fun changeBackgroundColor(isOpened: Boolean) {
        Log.d("TodoApp", "changeBackgroundColor $isOpened")

        animateBackgroundColor(isOpened)
        updateStatusBarColorTheme(isOpened)
    }

    private fun animateBackgroundColor(isOpened: Boolean) {
        val colorFrom = ContextCompat.getColor(this, R.color.white)
        val colorTo = ContextCompat.getColor(this, R.color.dark_blue)

        Log.d("DEBUG", "$isOpened")

        val colorAnimation = when (isOpened) {
            false -> ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            true -> ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
        }

        colorAnimation.duration = animationDuration

        colorAnimation.addUpdateListener {
            drawer.setBackgroundColor(it.animatedValue as Int)
            navView.setBackgroundColor(it.animatedValue as Int)
            this.window.statusBarColor = it.animatedValue as Int
        }
        colorAnimation.start()
    }

    private fun updateStatusBarColorTheme(isOpened: Boolean) {
        // todo Uopdate deprecated code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (isOpened) {
                true -> this.window.decorView.systemUiVisibility = drawer.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                false -> this.window.decorView.systemUiVisibility = drawer.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_notifications -> {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_categories -> {
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_analytics -> {
                Toast.makeText(this, "Slideshow", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Manage", Toast.LENGTH_SHORT).show()
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}