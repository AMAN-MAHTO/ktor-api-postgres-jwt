package com.example.routes

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.repository.TodoRepository
import com.example.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.userRouting(
    userDb:UserRepository,
    todoDb:TodoRepository,
    jwtService: JwtService,
    hash: (String)->String
)
{

    post("/v1/create") {
        // taking parameters
        val parameter = call.receive<Parameters>()
        val name = parameter["name"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val email = parameter["email"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val password = parameter["password"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)



        try {
            // convert paswd to hash
            val hashPaswd = hash(password)
            // creating user
            val currentUser = userDb.createUser(name,email,hashPaswd)


            currentUser?.userId?.let {

                // sotring userId in session
                call.sessions.set(MySession(it))

                // providing jwt token
                call.respondText {
                    jwtService.generateToken(currentUser)

                }

            }

        }catch (e:Exception){
            return@post call.respondText("${e.message}",status = HttpStatusCode.BadRequest)
        }
    }

    post("/v1/login") {

        // taking parameters
        val parameter = call.receive<Parameters>()
        val email = parameter["email"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val password = parameter["password"] ?: return@post call.respondText("Missing data",status = HttpStatusCode.Unauthorized)

        // convert paswd to hash
        val hashPaswd = hash(password)

        try {
            // find user
            val currentUser = userDb.findUserByEmail(email)
            currentUser?.userId?.let {
                //checking paswd
                if(currentUser.password == hashPaswd){
                    // storing userId in session
                    call.sessions.set(MySession(it))
                    // jwt token
                    call.respondText {
                        jwtService.generateToken(currentUser)
                    }
                }else{
                    call.respondText("incorrect password")
                }
            }

        }catch (e:Exception){
            return@post call.respondText("${e.message}",status = HttpStatusCode.BadRequest)
        }

    }

    delete ( "/v1/user" ){
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }

        if(user == null){
            call.respondText("User not found", status = HttpStatusCode.BadRequest)
        }
        try{
            if(user?.let { it1 -> userDb.deleteUser(it1.userId) } == 1){
                call.respondText("user deleted successfully")
            }else{
                call.respondText("error while deleting user")
            }

        }catch (e:Exception){
             call.respondText("${e.message}",status = HttpStatusCode.BadRequest)
        }
    }

    put("/v1/user") {
        // taking parameters
        val parameter = call.receive<Parameters>()
        val name = parameter["name"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val email = parameter["email"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)
        val password = parameter["password"] ?: return@put call.respondText("Missing data",status = HttpStatusCode.Unauthorized)

        // convert paswd to hash
        val hashPaswd = hash(password)

        val user = call.sessions.get<MySession>()?.let {
            userDb.findUserById(it.userId)
        }

        if(user == null){
            call.respondText("User not found", status = HttpStatusCode.BadRequest)
        }

        try {
            val updateResponse = user?.let { it1 -> userDb.updateUser(it1.userId, name, email,password) }
            if(updateResponse == 1){
                call.respondText("user updated successfully")
            }else{
                call.respondText("error while updating user")
            }
        }catch (e:Exception){
            call.respondText("${e.message}",status = HttpStatusCode.BadRequest)
        }
    }

}