package com.example.bookphoria.di

import com.example.bookphoria.data.remote.api.AuthApiService
import com.example.bookphoria.data.remote.api.BookApiService
import com.example.bookphoria.data.remote.api.FriendApiService
import com.example.bookphoria.data.remote.api.ShelfApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.gson.GsonBuilder

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("Accept-Encoding", "identity").build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        // Buat Gson dengan setLenient untuk mengatasi JSON yang tidak valid
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
//            .baseUrl("https://a3b6-2404-c0-2b10-00-2f8f-b895.ngrok-free.app/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookApiService(retrofit: Retrofit): BookApiService {
        return retrofit.create(BookApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): FriendApiService {
        return retrofit.create(FriendApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideShelfApiService(retrofit: Retrofit): ShelfApiServices {
        return retrofit.create(ShelfApiServices::class.java)
    }

}
