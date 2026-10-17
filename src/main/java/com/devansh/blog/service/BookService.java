package com.devansh.blog.service;

import com.devansh.blog.dto.BookMetaData;
import com.devansh.blog.dto.BookRequest;
import com.devansh.blog.dto.BookResponse;
import com.devansh.blog.dto.ChapterMetaData;
import com.devansh.blog.models.Book;
import com.devansh.blog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    private final ChapterService chapterService;

    private final MongoTemplate mongoTemplate;

    public BookMetaData getFeaturedBook(){
        Book book = bookRepository.findAll(PageRequest.of(0, 1)).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No featured book available"));
        return buildMetaData(book);
    }

    public Page<BookMetaData> getBooks(int page){
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must not be negative");
        }
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "id"));
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::buildMetaData);
    }

    public List<BookMetaData> searchBooks(String keyword){
        Document autocomplete = new Document()
                .append("query", keyword)
                .append("path", "title")
                .append("fuzzy", new Document()
                        .append("maxEdits", 1)
                        .append("prefixLength", 1)
                );

        Document searchStage = new Document(
                "$search",
                new Document().append("index", "book_search").append("autocomplete", autocomplete)
        );

        AggregationOperation searchOperation = context -> searchStage;
        Aggregation aggregation = Aggregation.newAggregation(searchOperation, Aggregation.limit(10));

        List<Book> books = mongoTemplate.aggregate(aggregation, "books", Book.class).getMappedResults();

        List<BookMetaData> bookMetaDataList = new ArrayList<>();
        for (Book book : books) {
            BookMetaData bookMetaData = buildMetaData(book);
            bookMetaDataList.add(bookMetaData);
        }
        return bookMetaDataList;
    }

    public BookResponse getBook(String bookId){
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return buildBookResponse(book);
    }

    public BookMetaData buildMetaData(Book book){

        BookMetaData bookMetaData = new BookMetaData();
        bookMetaData.setId(book.getId());
        bookMetaData.setTitle(book.getTitle());
        bookMetaData.setDescription(book.getDescription());
        bookMetaData.setImageUrl(book.getImageUrl());

        return bookMetaData;
    }

    public BookResponse buildBookResponse(Book book){
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setDescription(book.getDescription());
        bookResponse.setImageUrl(book.getImageUrl());
        List<ChapterMetaData> chapters = chapterService.getChapters(book.getId());
        bookResponse.setChapters(chapters);
        return bookResponse;
    }

    public BookMetaData createOrUpdateBook(BookRequest bookRequest){
        Book book = new Book();
        if(bookRequest.getId()!=null) {
             book = bookRepository.findById(bookRequest.getId()).orElse(new Book());
        }
        book.setTitle(bookRequest.getTitle());
        book.setDescription(bookRequest.getDescription());
        book.setImageUrl(bookRequest.getImageUrl());
        bookRepository.save(book);
        return buildMetaData(book);
    }

    public void addChapterToBook(String bookId, String chapterId){
        if (!bookRepository.existsById(bookId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        chapterService.assignToBook(chapterId, bookId);
    }

    public void deleteBook(String bookId){
        bookRepository.deleteById(bookId);
    }

}