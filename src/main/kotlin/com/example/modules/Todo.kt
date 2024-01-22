package com.example.modules

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
data class Todo(
    val id: Int,
    val userId: Int,
    val title: String,
    val isDone: Boolean
)


object TodoTable: Table(){
    val id:Column<Int> = integer("id").autoIncrement()
    val userId:Column<Int> = integer("userId").references(UserTable.userId)
    val title:Column<String> = varchar("title",524)
    val isDone:Column<Boolean> = bool("isDone")

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}


interface TodoDao{
    suspend fun getAllTodo(userId: Int): List<Todo>
    suspend fun getTodoById(id: Int): Todo?
    suspend fun createTodo(userId: Int,title: String,isDone: Boolean):Todo?
    suspend fun deleteTodo(id: Int):Int
    suspend fun deleteAllTodo(userId: Int):Int
    suspend fun updateTodo(id: Int,title: String,isDone: Boolean):Int

}