package com.gojekgithub.trending.ui.holder

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.gojekgithub.trending.databinding.TrendingItemBinding
import com.gojekgithub.trending.ui.model.TrendingItemViewModel

class TrendingItemViewHolder(
    private val binding: TrendingItemBinding,
    lifeCycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.lifecycleOwner = lifeCycleOwner
    }

    fun bindView(trendingItemViewModel: TrendingItemViewModel) {
        binding.model = trendingItemViewModel
        binding.executePendingBindings()
    }
}