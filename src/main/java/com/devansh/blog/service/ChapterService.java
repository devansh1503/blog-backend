package com.devansh.blog.service;

import com.devansh.blog.dto.ChapterMetaData;
import com.devansh.blog.dto.ChapterRequest;
import com.devansh.blog.models.Chapter;
import com.devansh.blog.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;

    public Chapter getChapterById(String id) {
        return chapterRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));
    }

    public List<ChapterMetaData> getChapters(String bookId){
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId);
        return chapters.stream().map(this::buildMetaData).toList();
    }

    public ChapterMetaData buildMetaData(Chapter chapter){
        ChapterMetaData chapterMetaData = new ChapterMetaData();
        chapterMetaData.setId(chapter.getId());
        chapterMetaData.setTitle(chapter.getTitle());
        chapterMetaData.setChapterNumber(chapter.getChapterNumber());
        chapterMetaData.setImageUrl(chapter.getImageUrl());
        return chapterMetaData;
    }

    public void assignToBook(String chapterId, String bookId) {
        Chapter chapter = getChapterById(chapterId);
        chapter.setBookId(bookId);
        chapterRepository.save(chapter);
    }

    public ChapterMetaData createOrUpdateChapter(ChapterRequest chapterRequest){
        Chapter chapter = new Chapter();
        if(chapterRequest.getId() != null){
            chapter =  chapterRepository.findById(chapterRequest.getId()).orElse(new Chapter());
        }

        chapter.setTitle(chapterRequest.getTitle());
        chapter.setBookId(chapterRequest.getBookId());
        chapter.setContent(chapterRequest.getContent());
        chapter.setImageUrl(chapterRequest.getImageUrl());
        chapter.setChapterNumber(chapterRequest.getChapterNumber());
        chapterRepository.save(chapter);
        return buildMetaData(chapter);
    }
}