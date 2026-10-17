package com.devansh.blog.dto;

import lombok.Data;

@Data
public class AddChapterRequest {
    private String bookId;
    private String chapterId;
}
