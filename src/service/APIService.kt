package com.ftadev.service

import com.ftadev.model.Book
import com.ftadev.model.BookImage
import com.ftadev.model.BookTable
import com.ftadev.service.DatabaseFactory.dbQuery
import model.ChangeType
import model.Notification
import org.jetbrains.exposed.sql.*

class APIService {

    private val listeners = mutableMapOf<Int, suspend (Notification<Book?>) -> Unit>()

    private suspend fun onChange(type: ChangeType, id: Int, entity: Book? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }

    suspend fun getAllBooks(limit: Int, offset: Int): List<Book> = dbQuery {
        BookTable.selectAll().limit(limit, offset = offset).map { toBook(it) }
    }

    suspend fun getAllImage(limit: Int, offset: Int): List<BookImage> = dbQuery {
        BookTable.selectAll().limit(limit, offset = offset * limit).map { toBookImage(it) }
    }

    suspend fun getWidget(id: Int): Book? = dbQuery {
        BookTable.select {
            (BookTable.id eq id)
        }.mapNotNull { toBook(it) }
            .singleOrNull()
    }

    suspend fun searchBook(name: String): List<Book> = dbQuery {
        BookTable.select {
            (BookTable.name like "%${name}%")
        }.map { toBook(it) }
    }

    suspend fun updateWidget(book: Book): Book? {
        val id = book.id
        return if (id == null) {
            addBook(book)
        } else {
            dbQuery {
                BookTable.update({ BookTable.id eq id }) {
                    it[name] = book.name
                    it[author] = book.author
                    it[translator] = book.translator!!
                    it[publisher] = book.publisher!!
                    it[category] = book.category!!
                    it[photo] = book.photo
                    it[descr] = book.descr
                    it[rate] = book.rate
                    it[pageNumber] = book.pageNumber!!
                    it[link] = book.link!!
                }
            }
            getWidget(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun addBook(book: Book): Book {
        var key = 0
        dbQuery {
            key = (BookTable.insert {
                it[name] = book.name
                it[author] = book.author
                it[translator] = book.translator!!
                it[publisher] = book.publisher!!
                it[category] = book.category!!
                it[photo] = book.photo
                it[descr] = book.descr
                it[rate] = book.rate
                it[pageNumber] = book.pageNumber!!
                it[link] = book.link!!
            } get BookTable.id)
        }
        return getWidget(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    suspend fun deleteBook(id: Int): Boolean = dbQuery {
            BookTable.deleteWhere { BookTable.id eq id } > 0
        }.also {
            if (it) onChange(ChangeType.DELETE, id)
        }

    private fun toBook(row: ResultRow): Book =
        Book(
            id = row[BookTable.id],
            name = row[BookTable.name],
            author = row[BookTable.author],
            translator = row[BookTable.translator],
            publisher = row[BookTable.publisher],
            category = row[BookTable.category],
            photo = row[BookTable.photo],
            descr = row[BookTable.descr],
            rate = row[BookTable.rate],
            pageNumber = row[BookTable.pageNumber],
            link = row[BookTable.link]
        )

    private fun toBookImage(row: ResultRow): BookImage =
        BookImage(
            id = row[BookTable.id],
            photo = row[BookTable.photo]
        )
}
