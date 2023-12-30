package com.atech.core.usecase

import com.atech.core.datasource.room.library.LibraryDao
import com.atech.core.datasource.room.library.LibraryModel
import javax.inject.Inject


data class LibraryUseCase @Inject constructor(
    val getAll: GetAll,
    val insertBook: InsertBook,
    val updateBook: UpdateBook,
    val deleteBook: DeleteBook,
    val deleteAll: DeleteAll,
    val isMarkAsDone: IsMarkAsDone
)

data class GetAll @Inject constructor(
    private val dao: LibraryDao
) {
    operator fun invoke() = dao.getAll()
}

data class InsertBook @Inject constructor(
    private val dao: LibraryDao
) {
    suspend operator fun invoke(book: LibraryModel) = dao.insertBook(book)
}

data class UpdateBook @Inject constructor(
    private val dao: LibraryDao
) {
    suspend operator fun invoke(book: LibraryModel) = dao.updateBook(book)
}

data class DeleteBook @Inject constructor(
    private val dao: LibraryDao
) {
    suspend operator fun invoke(book: LibraryModel) = dao.deleteBook(book)
}

data class DeleteAll @Inject constructor(
    private val dao: LibraryDao
) {
    suspend operator fun invoke() = dao.deleteAll()
}

data class IsMarkAsDone @Inject constructor(
    private val dao: LibraryDao
) {
    suspend operator fun invoke(
        book: LibraryModel,
    ) = dao.updateBook(
        book.copy(
            markAsReturn = !book.markAsReturn,
            eventId = -1,
            alertDate = -1
        )
    )
}