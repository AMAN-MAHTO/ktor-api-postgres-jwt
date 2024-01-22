package com.example.routes

import com.example.auth.MySession
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.todoRouting(
    userDb: UserRepository,
    todoDb:TodoRepository
)
{
    authenticate("auth-jwt") {
        post("/v1/todo") {
            val parameter = call.receive<Parameters>()
            val title = parameter["title"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
            val isDone = parameter["isDone"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)

            val user = call.sessions.get<MySession>()?.let {
                userDb.findUserById(it.userId)
            }

            if (user == null){
                return@post call.respondText("unauthorized user")
            }
            try {
                val todo = todoDb.createTodo(user.userId,title,isDone.toBoolean())
                if(todo == null){
                    call.respondText("unsuccessfull")
                }else{
                    call.respond(todo)
                }

            }catch (e:Throwable){
                call.respondText("${e.message}")
            }
        }
    }


    get("/v1/todo"){
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            return@get call.respondText("unauthorized user")
        }
        try {
            val allTodo = user?.let { it1 -> todoDb.getAllTodo(it1.userId) }
            if (allTodo != null) {
                if(allTodo.isNotEmpty()){
                    call.respond(allTodo)
                }else{
                    call.respondText("No todo to show!")
                }

            }
        }catch (e:Throwable){
            call.respondText("${e.message}")
        }

    }

    delete ("/v1/todo/{id}"){
        val id = call.parameters["id"]
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            return@delete call.respondText("unauthorized user")
        }
        try {
            val allTodo = user.let { it1 -> todoDb.getAllTodo(it1.userId) }
            allTodo.forEach {
                if(it.id == id?.toInt()){
                    val deleteResponse = todoDb.deleteTodo(id.toInt())
                    if( deleteResponse == 1){
                        return@delete call.respondText("deleted successfully")
                    }else{
                        return@delete call.respondText("problem deleting todo", status = HttpStatusCode.BadRequest)
                    }

                }
            }

            return@delete call.respondText("No todo exit")

        }catch (e:Throwable){
            return@delete call.respondText("${e.message}")
        }

    }

    delete("/v1/todo") {

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }
        if (user == null){
            return@delete call.respondText("unauthorized user")
        }
        try {
            val deleteResponse = todoDb.deleteAllTodo(user.userId)
            if( deleteResponse > 0 ){
                return@delete call.respondText("deleted successfully")
            }else{
                return@delete call.respondText("problem deleting all todo", status = HttpStatusCode.BadRequest)
            }
        }catch (e:Throwable){
            return@delete call.respondText("${e.message}")
        }
    }

    put("/v1/todo/{id?}") {
        val parameter = call.receive<Parameters>()
        val id = call.parameters["id"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val title = parameter["title"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val isDone = parameter["isDone"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }

        if (user == null){
            return@put call.respondText("unauthorized user")
        }
        try {
            val allTodo = user.let { it1 -> todoDb.getAllTodo(it1.userId) }
            allTodo.forEach {
                if(it.id == id.toInt()){
                    val deleteResponse =todoDb.updateTodo(id.toInt(),title,isDone.toBoolean())
                    if( deleteResponse == 1){
                        return@put call.respondText("updated successfully")
                    }else{
                        return@put call.respondText("problem updated all todo", status = HttpStatusCode.BadRequest)
                    }

                }
            }
            return@put call.respondText("no todo with given id")
        }catch (e:Throwable){
            call.respondText("${e.message}")
        }

    }
}