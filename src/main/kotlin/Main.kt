package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {

    val token ="EAAbPAueSpAUBQ15gG57Mas7q9iYSbDRu8AqgS7xlzWZAplsYmXqFTYqESbYvRxfAZBybXTutZCwDMJ3ijj44A3mM1swuBs2Qktjz9YswlZAABGRi69tpK0dxV0RzXlCByiwTyZBXZB4hUglU4efWxo0ciOClVGEvwJdOe3KqmxsDhneoTin549L5PPZC8aV9fFljgZA7ANzmCLmKHZCXsk6FaAzDOTicai7AGHDU6E0Ea9AYy57ZAmOhSYdpHVohUHaumYFQvhMYqytDGUV8o9AWHh"
    val numberId ="971935982677896"
    val verifyToken = "Maria123#"
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port = port) {
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



