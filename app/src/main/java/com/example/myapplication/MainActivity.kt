package com.example.myapplication
import MeFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityMainBinding
import fragment.CommunityFragment
import fragment.HomeFragment



class MainActivity : AppCompatActivity() {
    /*因为android app有生命周期
    所以入口点是onCreate，而不是main函数*/
    private val homeFragment = HomeFragment()
    private val communityFragment = CommunityFragment()
    private val meFragment = MeFragment()
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
                            .replace(R.id.fragment_container, communityFragment)
                            .commit()
                    true
                }

                R.id.navigation_me -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, meFragment)
                        .commit()
                    true
                }

                else -> {false}
            }
        }

    }
}