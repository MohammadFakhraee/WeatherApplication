package com.example.weatherapplication.di

import android.content.Context
import com.example.weatherapplication.R
import com.example.weatherapplication.data.source.api.CityApiDatasource
import com.example.weatherapplication.data.source.api.WeatherApiDataSource
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
    fun provideWeatherApiDataSource(retrofit: Retrofit): WeatherApiDataSource = retrofit.create(WeatherApiDataSource::class.java)

    @Provides
    @Singleton
    fun providesApiCityDatasource(retrofit: Retrofit): CityApiDatasource = retrofit.create(CityApiDatasource::class.java)
}