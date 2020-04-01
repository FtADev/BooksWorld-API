package com.ftadev.web

import com.ftadev.service.APIService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.widget(apiService: APIService) {
    route("/Book") {
        get("/") {
            call.respond(apiService.getAllBooks())
        }
        get("/{id}") {
            val widget = apiService.getWidget(call.parameters["id"]?.toInt()!!)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }
        post("/{category}") {
            val widget = apiService.getCategory(call.parameters["category"]!!)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }
    }
}
