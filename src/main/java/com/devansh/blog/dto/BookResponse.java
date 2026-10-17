package com.devansh.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookResponse {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private List<ChapterMetaData> chapters;
}
