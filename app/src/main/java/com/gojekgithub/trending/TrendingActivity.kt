package com.gojekgithub.trending

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gojekgithub.trending.databinding.TrendingLayoutBinding
import com.gojekgithub.trending.ui.main.MainFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class TrendingActivity : AppCompatActivity(), HasAndroidInjector {

    private lateinit var binding: TrendingLayoutBinding

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.trending_layout)

        binding.toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.more_black)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
                    .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_stars,
            R.id.action_name -> {
                val fragment = supportFragmentManager.findFragmentByTag(MainFragment.TAG)
                if (fragment is MainFragment) {
                    fragment.sortData(item.itemId)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }
}
