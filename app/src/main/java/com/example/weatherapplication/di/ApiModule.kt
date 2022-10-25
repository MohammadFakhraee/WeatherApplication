package com.example.weatherapplication.di

import android.content.Context
import com.example.weatherapplication.R
import com.example.weatherapplication.data.source.api.CityApi
import com.example.weatherapplication.data.source.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor {
                var request = it.request()
                val url = request.url().newBuilder().addQueryParameter("key", context.getString(R.string.weather_api_key)).build()
                request = request.newBuilder().url(url).build()
                it.proceed(request)
            }.build())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiDataSource(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun providesApiCityDatasource(retrofit: Retrofit): CityApi = retrofit.create(CityApi::class.java)
}