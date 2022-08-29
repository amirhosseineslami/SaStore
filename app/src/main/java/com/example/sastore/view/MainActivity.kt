package com.example.sastore.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sastore.R
import com.example.sastore.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.mainActivityBottomNav.setOnItemSelectedListener(this)
        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main_activity) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_nav_menu_home -> {
                navController.navigate(R.id.homeNavigationFragment)
            }
            R.id.main_nav_menu_account -> {
                navController.navigate(R.id.accountNavigationFragment)
            }
            R.id.main_nav_menu_shopping_cart -> {
                navController.navigate(R.id.shoppingCartNavigationFragment)
            }
        }
        return false
    }
}