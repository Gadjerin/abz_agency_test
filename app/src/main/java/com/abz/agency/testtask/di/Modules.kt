package com.abz.agency.testtask.di

import com.abz.agency.testtask.model.api.AbzAgencyUsersApi
import com.abz.agency.testtask.model.api.UsersApi
import com.abz.agency.testtask.model.api.UsersApi.Companion.BASE_URL
import com.abz.agency.testtask.model.api.abzUsersApiGson
import com.abz.agency.testtask.model.data.UsersRemoteDataSource
import com.abz.agency.testtask.model.data.UsersRepository
import com.abz.agency.testtask.model.data.UsersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindUsersRemoteDataSource(
        usersApi: UsersApi
    ): UsersRemoteDataSource

    @Binds
    abstract fun bindUsersRepository(
        usersRepositoryImpl: UsersRepositoryImpl
    ): UsersRepository
}
