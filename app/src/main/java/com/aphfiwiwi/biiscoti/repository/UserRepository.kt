package com.aphfiwiwi.biiscoti.repository

import com.aphfiwiwi.biiscoti.data.UserDao
import com.aphfiwiwi.biiscoti.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }
}