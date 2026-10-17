package com.devansh.blog.dto;

import lombok.Data;

@Data
public class BookRequest {
    String id;
    String title;
    String description;
    String imageUrl;
}
