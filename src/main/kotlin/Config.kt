package org.example

object Config {

    val whatsappToken: String =
        System.getenv("WHATSAPP_TOKEN")
            ?: error("WHATSAPP_TOKEN no está configurado")

    val phoneNumberId: String =
        System.getenv("PHONE_NUMBER_ID")
            ?: error("PHONE_NUMBER_ID no está configurado")

    val verifyToken: String =
        System.getenv("VERIFY_TOKEN")
            ?: error("VERIFY_TOKEN no está configurado")

    val port: Int =
        System.getenv("PORT")?.toInt() ?: 8080
}