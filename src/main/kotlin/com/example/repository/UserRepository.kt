package com.example.repository

import com.example.modules.User
import com.example.modules.UserTable
import com.example.modules.userDoa
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepository: userDoa {
    override suspend fun createUser(name: String, email: String, password: String): User? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = UserTable.insert {
                it[this.name] = name
                it[this.email] = email
                it[this.password] = password
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }


    override suspend fun findUserById(userId: Int): User? =
        DatabaseFactory.dbQuery {
            UserTable.select{UserTable.userId.eq(userId)}
                .map {
                    rowToUser(it)
                }.single()
        }


    override suspend fun findUserByEmail(email: String): User? =
        DatabaseFactory.dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }
            .map {
                rowToUser(it)
            }.single()
    }

    override suspend fun deleteUser(userId: Int): Int =
        DatabaseFactory.dbQuery {
            UserTable.deleteWhere {
                this.userId.eq(userId)
            }
        }

    override suspend fun updateUser(userId: Int, name: String, email: String, password: String): Int =
        DatabaseFactory.dbQuery {
            UserTable.update ({ UserTable.userId.eq(userId) }){
                it[this.email] = email
                it[this.name] = name
                it[this.password] =password

            }
        }


    private fun rowToUser(row: ResultRow?):User?{
        if(row == null){
            return null
        }

        return User(
            row[UserTable.userId],
            row[UserTable.name],
            row[UserTable.email],
            row[UserTable.password]
        )
    }
}