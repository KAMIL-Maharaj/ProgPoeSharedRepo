package com.example.budgettrackerapp111.repository

import com.example.budgettrackerapp111.data.User
import com.example.budgettrackerapp111.data.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}
