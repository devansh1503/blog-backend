package com.devansh.blog.models;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chapters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    @Id
    private String id;
    private String bookId;
    private String title;
    private String content;
    private String imageUrl;
    private Integer chapterNumber;
}
