package com.gojekgithub.trending.utils

import android.graphics.Color
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.ui.main.TrendingRecyclerViewAdapter


object BindingUtils {

    @BindingAdapter("app:trendingItems", "app:lifecycleOwner")
    @JvmStatic
    fun setTrendingAdapter(view: RecyclerView, items: List<GitRepositoryModel>?, lifecycleOwner: LifecycleOwner) {
        if(items == null) {
            return
        }
        var adapter: TrendingRecyclerViewAdapter? = view.adapter as TrendingRecyclerViewAdapter?
        view.layoutManager = LinearLayoutManager(view.context)
        if (null == adapter) {
            adapter = TrendingRecyclerViewAdapter(items, lifecycleOwner)
            view.adapter = adapter
        } else {
            adapter.setContentItemList(items)
            adapter.notifyDataSetChanged()
        }
    }

    @BindingAdapter("app:loadImage")
    @JvmStatic
    fun loadImage(imageView: AppCompatImageView, imageLink: String?) {
        Glide.with(imageView.context)
            .load(imageLink)
            .into(imageView)
    }

    @BindingAdapter("app:shimmer")
    @JvmStatic
    fun loadImage(shimmerFrameLayout: ShimmerFrameLayout, @Status status: Int?) {
        if(status == Status.Loading) {
            shimmerFrameLayout.visibility = VISIBLE
            shimmerFrameLayout.startShimmer()
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = GONE
        }
    }

    @BindingAdapter("app:backgroundColor")
    @JvmStatic
    fun setStatus(view: View, color: String?) {
        val color: Int = try {
            Color.parseColor(color)
        } catch (e: Exception) {
            Color.BLACK
        }
        view.setBackgroundColor(color)
    }

}