package com.example.repository

import com.example.modules.Todo
import com.example.modules.TodoDao
import com.example.modules.TodoTable
import com.example.modules.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRepository:TodoDao {
    override suspend fun createTodo(userId: Int, title: String, isDone: Boolean): Todo? {
        var statement:InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = TodoTable.insert {
                it[this.userId] = userId
                it[this.title] = title
                it[this.isDone] =isDone
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }
    override suspend fun getAllTodo(userId: Int): List<Todo> =
        DatabaseFactory.dbQuery {
            TodoTable.select { TodoTable.userId.eq(userId) }
                .mapNotNull {
                    rowToTodo(it)
                }
        }


    override suspend fun getTodoById(id: Int): Todo? =
        DatabaseFactory.dbQuery {
            TodoTable.select { TodoTable.id.eq(id)}
                .map { rowToTodo(it) }.single()
        }

    override suspend fun deleteTodo(id: Int): Int =
        DatabaseFactory.dbQuery {
            TodoTable.deleteWhere { this.id.eq(id) }
        }

    override suspend fun deleteAllTodo(userId: Int): Int =
        DatabaseFactory.dbQuery {
            TodoTable.deleteWhere {
                this.userId.eq(userId)
            }
        }

    override suspend fun updateTodo(id: Int, title: String, isDone: Boolean): Int =
        DatabaseFactory.dbQuery {
            TodoTable.update ({ TodoTable.id.eq(id) }){todo ->
                todo[TodoTable.isDone] = isDone
                todo[TodoTable.title] = title
            }
        }

    private fun rowToTodo(row: ResultRow?): Todo?{
        if(row == null){
            return null
        }
        else{
            return Todo(
                row[TodoTable.id],
                row[TodoTable.userId],
                row[TodoTable.title],
                row[TodoTable.isDone]
            )
        }
    }
}