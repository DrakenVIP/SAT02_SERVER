package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            post("/webhook") {
                val body = call.receiveText()
                println("Webhook Received: $body")
                call.respondText("EVENT_RECEIVED")
            }
            get("/") {
                call.respondText("Server running")
            }
        }
    }.start(wait = true)
}