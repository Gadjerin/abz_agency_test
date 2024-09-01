package com.abz.agency.testtask.model.api

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import javax.inject.Inject

interface AbzAgencyUsersApi {
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("count") count: Int? = null
    ): Response<GetUsersResponse>

    @GET("positions")
    suspend fun getPositions() : Response<GetPositionsResponse>

    @GET("token")
    suspend fun getToken(): Response<Token>

    @POST("users")
    @Multipart
    suspend fun postUser(
        @Header("Token") token: String,
        @Part("name") name: String,
        @Part("email") email: String,
        @Part("phone") phone: String,
        @Part("position_id") positionId: Int,
        @Part("photo") photo: RequestBody
    ): Response<PostUsersResponse>
}

class UsersApi @Inject constructor(private val api: AbzAgencyUsersApi) {
    companion object {
        const val BASE_URL = "https://frontend-test-assignment-api.abz.agency/api/v1/"
    }

    class UsersRequestSingleErrorException(message: String) : Exception(message)
    class UsersRequestMultipleErrorsException(message: String, val fails: Map<String, List<String>>)
        : Exception(message)
    class PositionsRequestException(message: String) : Exception(message)

    var totalUsers = -1
        private set

    suspend fun getUsers(page: Int, count: Int? = null): List<UserGet> {
        val response = api.getUsers(page, count)
        val successResponse = response.body()
        val errorResponse = response.errorBody()?.string()

        if (response.isSuccessful &&
            successResponse is GetUsersResponse.Success) {
            totalUsers = successResponse.totalUsers

            return successResponse.users
        }
        else {
            when(
                val error = abzUsersApiGson.fromJson(errorResponse, GetUsersResponse::class.java)
            ) {
                is GetUsersResponse.FailedSingleError -> {
                    throw UsersRequestSingleErrorException(error.message)
                }
                is GetUsersResponse.FailedMultipleErrors -> {
                    throw UsersRequestMultipleErrorsException(
                        error.message,
                        error.fails
                    )
                }
                else -> {
                    throw HttpException(
                        response
                    )
                }
            }
        }
    }

    suspend fun getPositions(): List<Position> {
        val response = api.getPositions()
        val successResponse = response.body()
        val errorResponse = response.errorBody()?.string()

        if (response.isSuccessful &&
            successResponse is GetPositionsResponse.Success) {
            return successResponse.positions
        }
        else {
            when(
                val error = abzUsersApiGson.fromJson(errorResponse, GetPositionsResponse::class.java)
            ) {
                is GetPositionsResponse.Failed -> {
                    throw PositionsRequestException(error.message)
                }
                else -> {
                    throw HttpException(
                        response
                    )
                }
            }
        }
    }

    private suspend fun getToken(): String {
        val response = api.getToken()
        val successResponse = response.body()

        if (response.isSuccessful &&
            successResponse is Token) {
            return successResponse.token
        } else {
            throw HttpException(
                response
            )
        }
    }

    suspend fun insertUser(user: UserPost): Int {
        val imageRequestBody = RequestBody
            .create(MediaType.parse("application/octet-stream"), user.photo)

        val response = api.postUser(
            getToken(),
            user.name,
            user.email,
            user.phone,
            user.positionId,
            imageRequestBody
        )
        val successResponse = response.body()
        val errorResponse = response.errorBody()?.string()

        if (response.isSuccessful &&
            successResponse is PostUsersResponse.Success) {
            return successResponse.userId
        }
        else {
            when(
                val error = abzUsersApiGson.fromJson(errorResponse, PostUsersResponse::class.java)
            ) {
                is PostUsersResponse.FailedSingleError -> {
                    throw UsersRequestSingleErrorException(error.message)
                }
                is PostUsersResponse.FailedMultipleErrors -> {
                    throw UsersRequestMultipleErrorsException(
                        error.message,
                        error.fails
                    )
                }
                else -> {
                    throw HttpException(
                        response
                    )
                }
            }
        }
    }
}


