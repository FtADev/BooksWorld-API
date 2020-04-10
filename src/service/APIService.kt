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
        BookTable.selectAll().limit(limit, offset = offset).map { toBookImage(it) }
    }

    suspend fun getWidget(id: Int): Book? = dbQuery {
        BookTable.select {
            (BookTable.id eq id)
        }.mapNotNull { toBook(it) }
            .singleOrNull()
    }

    suspend fun searchBook(name: String): Book? = dbQuery {
        BookTable.select {
            (BookTable.name like "%${name}")
        }.mapNotNull { toBook(it) }
            .singleOrNull()
    }

    suspend fun updateWidget(Book: Book): Book? {
        val id = Book.id
        return if (id == null) {
            addBook(Book)
        } else {
            dbQuery {
                BookTable.update({ BookTable.id eq id }) {
                    it[name] = Book.name
                    it[author] = Book.author
                    it[translator] = Book.translator!!
                    it[publisher] = Book.publisher!!
                    it[category] = Book.category!!
                    it[photo] = Book.photo
                    it[descr] = Book.descr
                    it[rate] = Book.rate
                    it[pageNumber] = Book.pageNumber!!
                    it[link] = Book.link!!
                }
            }
            getWidget(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun addBook(Book: Book): Book {
        var key = 0
        dbQuery {
            key = (BookTable.insert {
                it[name] = Book.name
                it[author] = Book.author
                it[translator] = Book.translator!!
                it[publisher] = Book.publisher!!
                it[category] = Book.category!!
                it[photo] = Book.photo
                it[descr] = Book.descr
                it[rate] = Book.rate
                it[pageNumber] = Book.pageNumber!!
                it[link] = Book.link!!
            } get BookTable.id)
        }
        return getWidget(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    suspend fun deleteBook(id: Int): Boolean {
        return dbQuery {
            BookTable.deleteWhere { BookTable.id eq id } > 0
        }.also {
            if (it) onChange(ChangeType.DELETE, id)
        }
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
