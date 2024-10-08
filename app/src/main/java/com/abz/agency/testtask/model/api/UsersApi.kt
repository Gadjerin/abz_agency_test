package com.abz.agency.testtask.model.api

import com.abz.agency.testtask.model.data.UsersRemoteDataSource
import okhttp3.MediaType
import okhttp3.MultipartBody
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
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("position_id") positionId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<PostUsersResponse>
}

class UsersApi @Inject constructor(private val api: AbzAgencyUsersApi) : UsersRemoteDataSource {
    companion object {
        const val BASE_URL = "https://frontend-test-assignment-api.abz.agency/api/v1/"
    }

    class UsersRequestSingleErrorException(message: String) : Exception(message)
    class UsersRequestMultipleErrorsException(message: String, val fails: Map<String, List<String>>)
        : Exception(message)
    class PositionsRequestException(message: String) : Exception(message)

    private var totalUsers = -1

    override suspend fun getUsersCount(): Int {
        if (totalUsers == -1) {
            // Unfortunately the only way to get total users if not initialized by other getUsers request yet
            getUsers(1, 1)
        }

        return totalUsers
    }

    override suspend fun getUsers(page: Int, count: Int?): List<UserGet> {
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

    override suspend fun getPositions(): List<Position> {
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

    override suspend fun insertUser(user: UserPost): Int {
        val photoRequestBody = RequestBody.create(
            MediaType.parse(
                "image/jpeg"
            ),
            user.photo
        )

        val response = api.postUser(
            getToken(),
            RequestBody.create(MultipartBody.FORM, user.name),
            RequestBody.create(MultipartBody.FORM, user.email),
            RequestBody.create(MultipartBody.FORM, user.phone),
            RequestBody.create(MultipartBody.FORM, user.positionId.toString()),
            MultipartBody.Part.createFormData("photo", "photo", photoRequestBody)
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


