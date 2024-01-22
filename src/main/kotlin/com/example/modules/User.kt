package com.example.modules

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val userId: Int,
    val email: String,
    val name: String,
    val password: String
) : Principal

object UserTable : Table(){
    val userId: Column<Int> = integer("userId").autoIncrement()
    val email: Column<String> = varchar("email",512).uniqueIndex()
    val name: Column<String> = varchar("name",512)
    val password: Column<String> = varchar("password",512)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}


interface userDoa {
    suspend fun createUser(name: String,email: String, password: String): User?
    suspend fun findUserById(userId: Int):User?
    suspend fun findUserByEmail(email: String):User?
    suspend fun deleteUser(userId: Int):Int
    suspend fun updateUser(userId: Int,name: String,email: String,password: String):Int
}