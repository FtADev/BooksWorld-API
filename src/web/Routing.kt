package com.ftadev.web

import com.ftadev.service.APIService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.widget(apiService: APIService) {
    route("/book") {
        get("/all/{limit}/{offset}") {
            call.respond(apiService.getAllBooks(call.parameters["limit"]?.toInt()!!, call.parameters["offset"]?.toInt()!!))
        }
        get("/image/{limit}/{offset}") {
            call.respond(apiService.getAllImage(call.parameters["limit"]?.toInt()!!, call.parameters["offset"]?.toInt()!!))
        }
        get("/info/{id}") {
            val widget = apiService.getWidget(call.parameters["id"]?.toInt()!!)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }
        get("/search/{name}") {
            val widget = apiService.searchBook(call.parameters["name"]!!)
            if (widget == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(widget)
        }
    }
}
