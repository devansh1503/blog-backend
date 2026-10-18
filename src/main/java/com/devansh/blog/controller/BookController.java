package com.devansh.blog.controller;

import com.devansh.blog.dto.AddChapterRequest;
import com.devansh.blog.dto.BookMetaData;
import com.devansh.blog.dto.BookRequest;
import com.devansh.blog.dto.BookResponse;
import com.devansh.blog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {
    private final BookService bookService;

    @GetMapping("/posts/getFeatured")
    public BookMetaData getFeatured(){
        return bookService.getFeaturedBook();
    }

    @GetMapping({"/posts", "/posts/"})
    public Page<BookMetaData> getBooks(@RequestParam int page) {
        return bookService.getBooks(page);
    }

    @GetMapping({"/book", "/book/"})
    public BookResponse getBook(@RequestParam String id) {
        return bookService.getBook(id);
    }

    @GetMapping({"/posts/search", "/posts/search/"})
    public List<BookMetaData> searchBooks(@RequestParam String query) {
        return bookService.searchBooks(query);
    }

    @RequestMapping(value = "/book", method = {RequestMethod.POST, RequestMethod.PUT})
    public BookMetaData createOrUpdateBook(@RequestBody BookRequest bookRequest) {
        return bookService.createOrUpdateBook(bookRequest);
    }

    @PostMapping("/book/addChapter")
    public void addChapter(@RequestBody AddChapterRequest addChapterRequest) {
        bookService.addChapterToBook(addChapterRequest.getBookId(), addChapterRequest.getChapterId());
    }

    @DeleteMapping
    public void deleteBook(@RequestParam String id) {
        bookService.deleteBook(id);
    }
}