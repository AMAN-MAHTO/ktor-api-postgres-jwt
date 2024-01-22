package com.example

import com.example.auth.JwtService
import com.example.auth.hashPassword
import com.example.plugins.*
import com.example.repository.DatabaseFactory
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()


    DatabaseFactory.init()
    val jwtService = JwtService()
    val userDb = UserRepository()
    val  todoDb = TodoRepository()
    val hash = ::hashPassword

    configureSecurity(jwtService,userDb)

    configureRouting(userDb,todoDb,jwtService,hash)
}
