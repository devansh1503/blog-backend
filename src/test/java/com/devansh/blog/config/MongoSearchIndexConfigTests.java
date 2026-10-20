package com.devansh.blog.config;

import com.mongodb.client.ListSearchIndexesIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class MongoSearchIndexConfigTests {
    private final MongoTemplate template = mock(MongoTemplate.class);
    @SuppressWarnings("unchecked")
    private final MongoCollection<Document> collection = mock(MongoCollection.class);
    @SuppressWarnings("unchecked")
    private final MongoCollection<Document> chapterCollection = mock(MongoCollection.class);
    @SuppressWarnings("unchecked")
    private final ListSearchIndexesIterable<Document> chapterIndexes = mock(ListSearchIndexesIterable.class);
    @SuppressWarnings("unchecked")
    private final ListSearchIndexesIterable<Document> indexes = mock(ListSearchIndexesIterable.class);

    private void initialize(List<Document> existingIndexes, List<Document> existingChapterIndexes) throws Exception {
        when(template.getCollection("books")).thenReturn(collection);
        when(template.getCollection("chapters")).thenReturn(chapterCollection);
        when(chapterCollection.listSearchIndexes()).thenReturn(chapterIndexes);
        doAnswer(invocation -> {
            List<Document> target = invocation.getArgument(0);
            target.addAll(existingChapterIndexes);
            return target;
        }).when(chapterIndexes).into(anyList());
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
        initialize(List.of(), List.of());

        var order = inOrder(template, collection);
        order.verify(template).createCollection("books");
        order.verify(template).getCollection("books");
        order.verify(collection).listSearchIndexes();
        order.verify(collection).createSearchIndexes(anyList());

        var chapterOrder = inOrder(template, chapterCollection);
        chapterOrder.verify(template).createCollection("chapters");
        chapterOrder.verify(template).getCollection("chapters");
        chapterOrder.verify(chapterCollection).listSearchIndexes();
        chapterOrder.verify(chapterCollection).createSearchIndexes(anyList());
    }

    @Test
    void preservesExistingCollectionAndSearchIndex() throws Exception {
        when(template.collectionExists("books")).thenReturn(true);
        when(template.collectionExists("chapters")).thenReturn(true);
        initialize(List.of(new Document("name", "book_search")),
                List.of(new Document("name", "chapter_search")));

        verify(template, never()).createCollection(anyString());
        verify(collection, never()).createSearchIndexes(anyList());
        verify(chapterCollection, never()).createSearchIndexes(anyList());
    }
}
