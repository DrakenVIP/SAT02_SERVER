package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

fun main() {

    val whatsappToken = Config.whatsappToken
    val phoneNumberId = Config.phoneNumberId
    val verifyToken = Config.verifyToken
    val port = Config.port

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    embeddedServer(Netty, port = port) {

        routing {

            // ✅ Ruta de prueba
            get("/") {
                call.respondText("Server running")
            }

            // ✅ Verificación del webhook (OBLIGATORIO PARA META)
            get("/webhook") {

                val mode = call.request.queryParameters["hub.mode"]
                val token = call.request.queryParameters["hub.verify_token"]
                val challenge = call.request.queryParameters["hub.challenge"]

                if (mode == "subscribe" && token == verifyToken) {
                    println("Webhook verificado correctamente")
                    call.respondText(challenge ?: "")
                } else {
                    call.respondText(
                        "Error de verificación",
                        status = HttpStatusCode.Forbidden
                    )
                }
            }

            // ✅ Recepción de mensajes
            post("/webhook") {

                val body = call.receiveText()
                println("Mensaje recibido: $body")

                try {
                    val json = Json.parseToJsonElement(body).jsonObject

                    val entry = json["entry"]?.jsonArray?.get(0)?.jsonObject
                    val changes = entry?.get("changes")?.jsonArray?.get(0)?.jsonObject
                    val value = changes?.get("value")?.jsonObject
                    val messages = value?.get("messages")?.jsonArray

                    if (messages != null) {

                        val messageObject = messages[0].jsonObject
                        val from = messageObject["from"]?.jsonPrimitive?.content
                        val text = messageObject["text"]
                            ?.jsonObject
                            ?.get("body")
                            ?.jsonPrimitive
                            ?.content

                        println("Mensaje de: $from")
                        println("Texto: $text")

                        // 🔥 RESPUESTA AUTOMÁTICA
                        if (from != null && text != null) {

                            val responseJson = """
                                {
                                  "messaging_product": "whatsapp",
                                  "to": "$from",
                                  "type": "text",
                                  "text": {
                                    "body": "Recibí tu mensaje: $text"
                                  }
                                }
                            """.trimIndent()

                            val response: HttpResponse = httpClient.post(
                                "https://graph.facebook.com/v22.0/$phoneNumberId/messages"
                            ) {
                                header("Authorization", "Bearer $whatsappToken")
                                contentType(ContentType.Application.Json)
                                setBody(responseJson)
                            }

                            println("Respuesta enviada: ${response.status}")
                        }
                    }

                } catch (e: Exception) {
                    println("Error procesando mensaje: ${e.message}")
                }

                call.respondText("EVENT_RECEIVED")
            }
        }

    }.start(wait = true)
}