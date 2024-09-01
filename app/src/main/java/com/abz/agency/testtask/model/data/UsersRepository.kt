package com.abz.agency.testtask.model.data

import com.abz.agency.testtask.model.api.Position
import com.abz.agency.testtask.model.api.UserGet
import com.abz.agency.testtask.model.api.UserPost
import javax.inject.Inject

interface UsersRepository {
    suspend fun getUsersCount(): Int
    suspend fun getUsers(page: Int, count: Int? = null): List<UserGet>
    suspend fun getPositions(): List<Position>
    suspend fun insertUser(user: UserPost): Int
}

class UsersRepositoryImpl @Inject constructor(
    private val remoteDataSource: UsersRemoteDataSource
) : UsersRepository {

    override suspend fun getUsersCount(): Int {
        return remoteDataSource.getUsersCount()
    }

    override suspend fun getUsers(page: Int, count: Int?): List<UserGet> {
        return remoteDataSource.getUsers(page, count)
    }

    override suspend fun getPositions(): List<Position> {
        return remoteDataSource.getPositions()
    }

    override suspend fun insertUser(user: UserPost): Int {
        return remoteDataSource.insertUser(user)
    }
}
