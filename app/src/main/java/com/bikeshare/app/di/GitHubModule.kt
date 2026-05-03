package com.bikeshare.app.di

import com.bikeshare.app.BuildConfig
import com.bikeshare.app.data.api.github.GitHubApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitHubModule {

    @Provides
    @Singleton
    fun provideGitHubApiService(moshi: Moshi): GitHubApiService {
        // GitHub strongly recommends a meaningful User-Agent and applies stricter rate
        // limits to anonymous calls without one. Accept header pins the API version so
        // future GitHub-side schema changes don't surprise us.
        val userAgent =
            "${BuildConfig.APP_NAME}/${BuildConfig.VERSION_NAME} (${BuildConfig.APPLICATION_ID})"
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GitHubApiService::class.java)
    }
}
