package com.example.plugins

import com.example.auth.JwtService
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import com.example.routes.todoRouting
import com.example.routes.userRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDb: UserRepository,
    todoDb: TodoRepository,
    jwtService: JwtService,
    hash: (String) -> String
) {
    routing {
        userRouting(userDb,todoDb,jwtService,hash)
        todoRouting(userDb,todoDb)
        get("/"){
            call.respondText("todo api" +
                    "\n/v1/create --> create new user" +
                    "\n/v1/login --> login user\n" +
                    "/v1/user --> delete and update")
        }
    }
}
