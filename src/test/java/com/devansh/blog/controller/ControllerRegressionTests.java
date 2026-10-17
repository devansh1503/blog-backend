package com.devansh.blog.controller;

import com.devansh.blog.service.BookService;
import com.devansh.blog.service.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerRegressionTests {
    private final BookService books = mock(BookService.class);
    private final ChapterService chapters = mock(ChapterService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new BookController(books), new ChapterController(chapters)).build();

    @Test
    void documentedReadRoutesResolve() throws Exception {
        mvc.perform(get("/v1/posts/getFeatured")).andExpect(status().isOk());
        mvc.perform(get("/v1/posts/").param("page", "0")).andExpect(status().isOk());
        mvc.perform(get("/v1/posts/search/").param("query", "title")).andExpect(status().isOk());
        mvc.perform(get("/v1/book/").param("id", "book")).andExpect(status().isOk());
        mvc.perform(get("/v1/chapter/").param("id", "chapter")).andExpect(status().isOk());
        verify(books).getBook("book");
        verify(chapters).getChapterById("chapter");
    }

    @Test
    void chapterAndBookWritesAreExposed() throws Exception {
        mvc.perform(post("/v1/chapter").contentType("application/json").content("{\"title\":\"Chapter\"}"))
                .andExpect(status().isOk());
        mvc.perform(put("/v1/chapter").contentType("application/json").content("{\"id\":\"chapter\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/v1/book").contentType("application/json").content("{\"title\":\"Book\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/v1/book/addChapter").contentType("application/json")
                .content("{\"bookId\":\"book\",\"chapterId\":\"chapter\"}"))
                .andExpect(status().isOk());
        verify(chapters, times(2)).createOrUpdateChapter(any());
        verify(books).addChapterToBook("book", "chapter");
    }
}
