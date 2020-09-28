package com.gojekgithub.trending.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.gojekgithub.trending.R
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.databinding.TrendingItemBinding
import com.gojekgithub.trending.ui.holder.TrendingItemViewHolder
import com.gojekgithub.trending.ui.model.TrendingItemViewModel

class TrendingRecyclerViewAdapter(
    private var trendingRepoList: List<GitRepositoryModel>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TrendingItemViewHolder>() {

    override fun getItemCount(): Int {
        return trendingRepoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingItemViewHolder {
        val binding: TrendingItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.trending_item, parent, false
        )
        return TrendingItemViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: TrendingItemViewHolder, position: Int) {
        val trendingRepoData: GitRepositoryModel = trendingRepoList!![position]
        holder.bindView(TrendingItemViewModel(trendingRepoData))
    }

    fun setContentItemList(items: List<GitRepositoryModel>) {
        trendingRepoList = items
    }
}
