package com.chesire.nekome.datasource.user

import com.chesire.nekome.core.flags.Service
import com.chesire.nekome.database.dao.UserDao
import com.chesire.nekome.datasource.user.remote.UserApi
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Repository to interact with user data.
 */
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi,
    private val map: UserMapper
) {

    /**
     * Retrieve the information about a user.
     */
    val user: Flow<User>
        get() = userDao.getUser(Service.Kitsu)
            .map { if (it == null) User.NotFound else User.Found(map.toUserDomain(it)) }
            .catch { User.NotFound }

    /**
     * Updates the stored user in the database, data will be funneled to the [user].
     */
    suspend fun refreshUser(): Result<Unit, Unit> {
        println("okhttp: [UserRepository] refreshUser() - Calling userApi.getUserDetails()...")
        return userApi.getUserDetails()
            .onSuccess {
                println("okhttp: [UserRepository] refreshUser() - API success, user: id=${it.userId}, name=${it.name}")
                val model = map.toUserEntity(it)
                userDao.insert(model)
                println("okhttp: [UserRepository] refreshUser() - User saved to DB")
            }
            .onFailure {
                println("okhttp: [UserRepository] refreshUser() - FAILED: API error")
                Timber.e("Error refreshing user")
            }
            .mapEither(
                success = { },
                failure = { }
            )
    }

    /**
     * Retrieves the user id from the database.
     */
    suspend fun retrieveUserId(): Int? {
        val userId = userDao.retrieveUserId(Service.Kitsu)
        println("okhttp: [UserRepository] retrieveUserId() - Result: $userId")
        return userId
    }
}
