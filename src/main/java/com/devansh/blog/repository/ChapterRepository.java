package com.devansh.blog.repository;

import com.devansh.blog.models.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChapterRepository extends MongoRepository<Chapter, String> {
    List<Chapter> findByBookIdOrderByChapterNumberAsc(String bookId);
}
