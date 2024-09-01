package com.abz.agency.testtask.di

import com.abz.agency.testtask.model.api.AbzAgencyUsersApi
import com.abz.agency.testtask.model.api.UsersApi.Companion.BASE_URL
import com.abz.agency.testtask.model.api.abzUsersApiGson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAbzAgencyUsersApi(retrofit: Retrofit): AbzAgencyUsersApi {
        return retrofit.create(AbzAgencyUsersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    // gson instance with all necessary type adapters
                    abzUsersApiGson
                )
            )
            .build()
    }
}