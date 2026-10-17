package com.devansh.blog.dto;

import lombok.Data;

@Data
public class ChapterMetaData {
    private String id;
    private String title;
    private String imageUrl;
    private Integer chapterNumber;
}
