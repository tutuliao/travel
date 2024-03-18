package com.example.myapplication
import fragment.MeFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityMainBinding


import fragment.CollectFragment
import fragment.HomeFragment
import fragment.SearchFragment


class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val collectFragment = CollectFragment()
    private val meFragment = MeFragment()
    private val searchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        //隐藏actionbar
        supportActionBar?.hide()

        // 默认显示 HomeFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .commit()

        //底部导航点击事件
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, homeFragment)
                            .commit()
                    true
                }

                R.id.navigation_hotel -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, collectFragment)
                            .commit()
                    true
                }

                R.id.navigation_me -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, meFragment)
                        .commit()
                    true
                }
                
                R.id.navigation_search ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, searchFragment)
                        .commit()
                    true
                }
                else -> {false}
            }
        }

    }
}