package com.abz.agency.testtask.model.data

import com.abz.agency.testtask.model.api.Position
import com.abz.agency.testtask.model.api.UserGet
import com.abz.agency.testtask.model.api.UserPost

interface UsersRemoteDataSource {
    suspend fun getUsersCount(): Int
    suspend fun getUsers(page: Int, count: Int? = null): List<UserGet>
    suspend fun getPositions(): List<Position>
    suspend fun insertUser(user: UserPost): Int
}
