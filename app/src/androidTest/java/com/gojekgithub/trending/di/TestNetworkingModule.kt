package com.gojekgithub.trending.di

import android.content.Context
import com.gojekgithub.trending.BuildConfig
import com.gojekgithub.trending.data.api.TrendingApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class TestNetworkingModule {

    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer {
        var mockWebServer = MockWebServer()
        mockWebServer.start()
        return mockWebServer
    }

    @Singleton
    @Provides
    fun provideHttpUrl(mockWebServer: MockWebServer): HttpUrl? {
        val latch = CountDownLatch(1)
        var url: HttpUrl? = null
        CoroutineScope(Dispatchers.IO).launch {
            url = mockWebServer.url("/")
            latch.countDown()
        }
        latch.await()
        return url
    }


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
                    .maxStale(2, TimeUnit.HOURS)
                    .build()
            response.newBuilder()
                .removeHeader(TrendingNetworkingModule.HEADER_PRAGMA)
                .removeHeader(TrendingNetworkingModule.HEADER_CACHE_CONTROL)
                .header(TrendingNetworkingModule.HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    private fun provideRequestCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request: Request = chain.request()
            val forceCache = request.header(TrendingApiService.HEADER_FORCE_REMOTE)
            if (forceCache.equals("true", true)) {
                request = request.newBuilder()
                    .removeHeader(TrendingNetworkingModule.HEADER_CACHE_CONTROL)
                    .build()
            } else {
                val cacheControl = CacheControl.Builder()
                    .maxStale(2, TimeUnit.HOURS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(TrendingNetworkingModule.HEADER_PRAGMA)
                    .removeHeader(TrendingNetworkingModule.HEADER_CACHE_CONTROL)
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
        httpUrl: HttpUrl?
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(httpUrl!!)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): TrendingApiService =
        retrofit.create(TrendingApiService::class.java)

}