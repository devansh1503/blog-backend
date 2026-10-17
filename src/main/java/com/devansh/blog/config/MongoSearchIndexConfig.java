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

            var collection = mongoTemplate.getCollection("books");

            boolean indexExists = collection
                    .listSearchIndexes()
                    .into(new java.util.ArrayList<>())
                    .stream()
                    .anyMatch(index ->
                            "book_search".equals(index.getString("name"))
                    );

            if (!indexExists) {
                collection.createSearchIndexes(
                        List.of(new SearchIndexModel("book_search", definition))
                );
            }
        };
    }
}