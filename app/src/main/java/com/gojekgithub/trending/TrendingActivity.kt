package com.gojekgithub.trending

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import com.gojekgithub.trending.databinding.TrendingLayoutBinding
import com.gojekgithub.trending.ui.main.MainFragment
import kotlinx.android.synthetic.main.trending_layout.view.*


class TrendingActivity : AppCompatActivity() {

    private lateinit var binding: TrendingLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrendingLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        view.toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.more_black)
        setSupportActionBar(view.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
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
            R.id.action_stars -> true
            R.id.action_name -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
