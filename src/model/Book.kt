package com.ftadev.model

import org.jetbrains.exposed.sql.Table

object BookTable: Table() {
        val id = integer("id").primaryKey().autoIncrement()
        val name = varchar("name", 1000)
        val author = varchar("author", 100)
        val translator = varchar("translator", 100)
        val publisher = varchar("publisher", 100)
        val category = varchar("category", 100)
        val photo = text("photo")
        val descr = text("descr")
        val rate = double("rate")
        val pageNumber = integer("page_nr")
        val link = text("link")
}

data class Book(
        val id: Int?,
        val name: String,
        val author: String,
        val translator: String?,
        val publisher: String?,
        val category: String?,
        val photo: String,
        val descr: String,
        val rate: Double,
        val pageNumber: Int?,
        val link: String?
)
