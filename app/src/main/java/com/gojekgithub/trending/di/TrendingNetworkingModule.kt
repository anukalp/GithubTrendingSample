package com.gojekgithub.trending.di

import android.content.Context
import com.gojekgithub.trending.BuildConfig
import com.gojekgithub.trending.data.api.TrendingApiService
import com.gojekgithub.trending.data.api.TrendingApiService.Companion.HEADER_FORCE_REMOTE
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class TrendingNetworkingModule {

    companion object{
        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"
    }


    @Provides
    @Singleton
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(provideResponseCacheInterceptor())
            .addInterceptor(provideRequestCacheInterceptor())
            .cache(cache)
            .retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    } else OkHttpClient
        .Builder()
        .cache(cache)
        .addInterceptor(provideResponseCacheInterceptor())
        .addInterceptor(provideRequestCacheInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()


    private fun provideResponseCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = CacheControl.Builder()
                    .maxAge(2, TimeUnit.HOURS)
                    .maxStale(2, TimeUnit.HOURS)
                    .build()
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    private fun provideRequestCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request: Request = chain.request()
            val forceCache = request.header(HEADER_FORCE_REMOTE)
            request = if (forceCache.equals("true", true)) {
                val cacheControl = CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build()
                request.newBuilder()
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            } else {
                val cacheControl = CacheControl.Builder()
                    .maxStale(2, TimeUnit.HOURS)
                    .build()
                request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): TrendingApiService =
        retrofit.create(TrendingApiService::class.java)

}