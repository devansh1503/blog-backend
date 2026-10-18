package com.devansh.blog.config;

import com.mongodb.client.model.SearchIndexModel;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MongoSearchIndexConfig {

    private final MongoTemplate mongoTemplate;

    @Bean
    public ApplicationRunner initializeSearchIndex() {
        return args -> {

            Document definition = Document.parse("""
                {
                  "mappings": {
                    "dynamic": false,
                    "fields": {
                      "title": [
                        {
                          "type": "autocomplete",
                          "tokenization": "nGram",
                          "minGrams": 3,
                          "maxGrams": 15
                        },
                        {
                          "type": "string"
                        }
                      ]
                    }
                  }
                }
                """);

            if (!mongoTemplate.collectionExists("books")) {
                mongoTemplate.createCollection("books");
            }
            if(!mongoTemplate.collectionExists("chapters")) {
                mongoTemplate.createCollection("chapters");
            }
            var collection = mongoTemplate.getCollection("books");
            var chapterCollection = mongoTemplate.getCollection("chapters");

            boolean indexExists = collection
                    .listSearchIndexes()
                    .into(new java.util.ArrayList<>())
                    .stream()
                    .anyMatch(index ->
                            "book_search".equals(index.getString("name"))
                    );

            boolean chapterIndexExists = chapterCollection
                    .listSearchIndexes()
                    .into(new java.util.ArrayList<>())
                    .stream()
                    .anyMatch(index ->
                            "chapter_search".equals(index.getString("name"))
                    );

            if (!indexExists) {
                collection.createSearchIndexes(
                        List.of(new SearchIndexModel("book_search", definition))
                );
            }
            if (!chapterIndexExists) {
                chapterCollection.createSearchIndexes(
                        List.of(new SearchIndexModel("chapter_search", definition))
                );
            }
        };
    }
}
