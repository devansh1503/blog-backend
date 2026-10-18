package com.devansh.blog.config;

import com.mongodb.client.ListSearchIndexesIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class MongoSearchIndexConfigTests {
    private final MongoTemplate template = mock(MongoTemplate.class);
    @SuppressWarnings("unchecked")
    private final MongoCollection<Document> collection = mock(MongoCollection.class);
    private final ListSearchIndexesIterable<Document> indexes = mock(ListSearchIndexesIterable.class);

    private void initialize(List<Document> existingIndexes) throws Exception {
        when(template.getCollection("books")).thenReturn(collection);
        when(collection.listSearchIndexes()).thenReturn(indexes);
        doAnswer(invocation -> {
            List<Document> target = invocation.getArgument(0);
            target.addAll(existingIndexes);
            return target;
        }).when(indexes).into(anyList());
        new MongoSearchIndexConfig(template).initializeSearchIndex()
                .run(new DefaultApplicationArguments(new String[0]));
    }

    @Test
    void createsMissingCollectionBeforeSearchIndexOperations() throws Exception {
        initialize(List.of());

        var order = inOrder(template, collection);
        order.verify(template).createCollection("books");
        order.verify(template).getCollection("books");
        order.verify(collection).listSearchIndexes();
        order.verify(collection).createSearchIndexes(anyList());
    }

    @Test
    void preservesExistingCollectionAndSearchIndex() throws Exception {
        when(template.collectionExists("books")).thenReturn(true);
        initialize(List.of(new Document("name", "book_search")));

        verify(template, never()).createCollection(anyString());
        verify(collection, never()).createSearchIndexes(anyList());
    }
}
