package com.devansh.blog.service;

import com.devansh.blog.models.Book;
import com.devansh.blog.models.Chapter;
import com.devansh.blog.repository.BookRepository;
import com.devansh.blog.repository.ChapterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRegressionTests {
    private final BookRepository books = mock(BookRepository.class);
    private final ChapterRepository chapters = mock(ChapterRepository.class);
    private final ChapterService chapterService = new ChapterService(chapters, mock(MongoTemplate.class));
    private final BookService bookService = new BookService(books, chapterService, null);

    @Test
    void emptyFeaturedReturns404() {
        when(books.findAll(any(Pageable.class))).thenReturn(Page.empty());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                bookService::getFeaturedBook).getStatusCode().value());
    }

    @Test
    void unknownRecordsReturn404() {
        when(books.findById("missing")).thenReturn(Optional.empty());
        when(chapters.findById("missing")).thenReturn(Optional.empty());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> bookService.getBook("missing")).getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> chapterService.getChapterById("missing")).getStatusCode().value());
    }

    @Test
    void linkingUpdatesChapterAndBookResponseReadsIt() {
        Book book = new Book();
        book.setId("book");
        Chapter chapter = new Chapter();
        chapter.setId("chapter");
        when(books.existsById("book")).thenReturn(true);
        when(chapters.findById("chapter")).thenReturn(Optional.of(chapter));
        bookService.addChapterToBook("book", "chapter");
        assertEquals("book", chapter.getBookId());
        verify(chapters).save(chapter);
        when(books.findById("book")).thenReturn(Optional.of(book));
        when(chapters.findByBookIdOrderByChapterNumberAsc("book"))
                .thenReturn(java.util.List.of(chapter));
        assertEquals("chapter", bookService.getBook("book").getChapters().getFirst().getId());
    }

    @Test
    void missingBookCannotReceiveChapter() {
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> bookService.addChapterToBook("missing", "chapter")).getStatusCode().value());
        verifyNoInteractions(chapters);
    }

    @Test
    void negativePageReturns400() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> bookService.getBooks(-1)).getStatusCode().value());
    }
}
