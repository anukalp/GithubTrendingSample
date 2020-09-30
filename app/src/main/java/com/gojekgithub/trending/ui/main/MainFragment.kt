package com.gojekgithub.trending.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gojekgithub.trending.R
import com.gojekgithub.trending.databinding.MainFragmentBinding
import com.gojekgithub.trending.ui.callbacks.TrendingRetryListener
import com.gojekgithub.trending.ui.model.MainViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : Fragment(), TrendingRetryListener {

    companion object {
        fun newInstance() = MainFragment()
        const val TAG = "MainFragment"
    }

    private lateinit var binding: MainFragmentBinding

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false
        )
        binding.swipeContainer.setOnRefreshListener {
            fetchData()                    // refresh your list contents somehow
            swipeContainer.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }
        binding.lifecycleOwner = this
        binding.owner = this
        binding.itemViewModel = viewModel
        binding.retryCallback = this
        return binding.root
    }

    fun sortData(itemId : Int){
        viewModel.sortData(itemId)
    }

    override fun fetchData() {
        viewModel.fetchGitRepos(true)
    }

}