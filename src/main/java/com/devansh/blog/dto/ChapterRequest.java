package com.devansh.blog.dto;

import lombok.Data;

@Data
public class ChapterRequest {
    private String id;
    private String title;
    private String content;
    private String bookId;
    private String imageUrl;
    private Integer chapterNumber;
}
