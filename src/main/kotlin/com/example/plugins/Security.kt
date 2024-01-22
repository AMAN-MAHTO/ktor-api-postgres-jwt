package com.example.plugins

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureSecurity(jwtService: JwtService, userDb: UserRepository) {

    val jwtRealm = "ktor sample app"



    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm //basical a tag name
            verifier(jwtService.verifier) // verifie

            validate {
                val payload = it.payload
                val claim = payload.getClaim("userId")
                val claimInt = claim.asInt()
                val user = userDb.findUserById(claimInt)
                user
            }
        }
    }

    //
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

}
