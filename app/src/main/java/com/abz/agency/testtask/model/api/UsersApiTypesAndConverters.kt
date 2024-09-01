package com.abz.agency.testtask.model.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Represents User data which you get by `GET /users`
 */
data class UserGet(
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val photo: String
)

/**
 * Represents User data needed for new user creation `POST /users`
 */
data class UserPost(
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("position_id") val positionId: Int,
    val photo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPost

        if (name != other.name) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (positionId != other.positionId) return false
        return photo.contentEquals(other.photo)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + positionId
        result = 31 * result + photo.contentHashCode()
        return result
    }
}

/**
 * Possible `GET /users` responses
 */
sealed interface GetUsersResponse {
    data class Success(
        @SerializedName("total_users") val totalUsers: Int,
        val users: List<UserGet>
    ) : GetUsersResponse

    data class FailedSingleError(
        val message: String
    ) : GetUsersResponse

    data class FailedMultipleErrors(
        val message: String,
        val fails: Map<String, List<String>>
    ) : GetUsersResponse
}

private object GetUsersResponseDeserializer : JsonDeserializer<GetUsersResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): GetUsersResponse {
        val jsonObject = json.asJsonObject

        return if (jsonObject["success"].asBoolean) {
            Gson().fromJson(json, GetUsersResponse.Success::class.java)
        }
        else if (jsonObject.has("fails")) {
            Gson().fromJson(json, GetUsersResponse.FailedMultipleErrors::class.java)
        }
        else {
            Gson().fromJson(json, GetUsersResponse.FailedSingleError::class.java)
        }
    }
}

/**
 * Possible `POST /users` responses
 */
sealed interface PostUsersResponse {
    data class Success(
        @SerializedName("user_id") val userId: Int,
        val message: String
    ) : PostUsersResponse

    data class FailedSingleError(
        val message: String
    ) : PostUsersResponse

    data class FailedMultipleErrors(
        val message: String,
        val fails: Map<String, List<String>>
    ) : PostUsersResponse
}

private object PostUsersResponseDeserializer : JsonDeserializer<PostUsersResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PostUsersResponse {
        val jsonObject = json.asJsonObject

        return if (jsonObject["success"].asBoolean) {
            Gson().fromJson(json, PostUsersResponse.Success::class.java)
        } else if (jsonObject.has("fails")) {
            Gson().fromJson(json, PostUsersResponse.FailedMultipleErrors::class.java)
        } else {
            Gson().fromJson(json, PostUsersResponse.FailedSingleError::class.java)
        }
    }
}

/**
 * Represents Position data which you get by `GET /positions`
 */
data class Position(
    val id: Int,
    val name: String
)

/**
 * Possible `GET /positions` responses
 */
sealed interface GetPositionsResponse {
    data class Success(
        val positions: List<Position>
    ) : GetPositionsResponse

    data class Failed(
        val message: String
    ) : GetPositionsResponse
}

private object GetPositionsResponseDeserializer : JsonDeserializer<GetPositionsResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): GetPositionsResponse {
        val jsonObject = json.asJsonObject

        return if (jsonObject["success"].asBoolean) {
            Gson().fromJson(json, GetPositionsResponse.Success::class.java)
        } else {
            Gson().fromJson(json, GetPositionsResponse.Failed::class.java)
        }
    }
}

data class Token(val token: String)

/**
 * Gson object which contains all necessary type adapters to convert all possible responses to objects.
 */
val abzUsersApiGson: Gson = GsonBuilder()
    .registerTypeAdapter(
        GetUsersResponse::class.java,
        GetUsersResponseDeserializer
    )
    .registerTypeAdapter(
        PostUsersResponse::class.java,
        PostUsersResponseDeserializer
    )
    .registerTypeAdapter(
        GetPositionsResponse::class.java,
        GetPositionsResponseDeserializer
    )
    .create()
