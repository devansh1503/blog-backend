package com.devansh.blog.controller;

import com.devansh.blog.dto.AddChapterRequest;
import com.devansh.blog.dto.ChapterMetaData;
import com.devansh.blog.dto.ChapterRequest;
import com.devansh.blog.models.Chapter;
import com.devansh.blog.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/v1/chapter", "/v1/chapter/"})
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping
    public Chapter getChapter(@RequestParam String id) {
        return chapterService.getChapterById(id);
    }

    @GetMapping("/all")
    public List<ChapterMetaData> getChapters(){
        return chapterService.getAllChapters();
    }

    @GetMapping("/search")
    public List<ChapterMetaData> searchChapters(@RequestParam String keyword){
        return chapterService.searchChapters(keyword);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public ChapterMetaData createOrUpdateChapter(@RequestBody ChapterRequest request) {
        return chapterService.createOrUpdateChapter(request);
    }

    @DeleteMapping
    public void deleteChapter(@RequestParam String id) {
        chapterService.deleteChapter(id);
    }
}
