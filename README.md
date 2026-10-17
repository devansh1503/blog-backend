Hi, Devansh this side, here is the structure for this project-

# Requirements
* User and Admin seperate flows
* User must be able to see featured post, paginated view of all posts
* User must be able to search across the posts
* User must be able to view chapters and contents
* User must be able to read a chapter

* Admin must be able to view, create, update posts
* The post may be containing images, links to other posts etc.

# Entities

* Post - interface
* Book, Chapter
* Book - List of Chapters, Title, Synopsis, cover image
* Chapter - Title, Content

# APIs-

1. GET - /v1/posts/getFeatured
2. GET - /v1/posts/?page={page_num} - views books
3. GET - /v1/book/?id={book_id} - views 1 book
4. GET - /v1/chapter/?id={chapter_id} - views 1 chapter
5. GET - /v1/posts/search/?query={query} - searches
6. PUT/POST - /v1/book - Book Details  (Authenticated)
7. POST - /v1/chapter - Chapter title and content (Authenticated)
8. POST - /v1/book/addChapter - Adding a chapter in a book (Authenticated)



Deep Dives - 

1. For storing a chapter - title can be string, content can be HTML 
2. For storing a book - simple
3. Mongo DB - Book and Chapter
4. Open Search - Search database
5. CDC + Kafka for syncing chapters asynchronously


Services -

1. BookService - getFeaturedBook, getPaginatedBooks, searchBooks, getBook, createBook, addChapter
2. ChapterService - getChapter, addChapter, updateChapter, deleteChapter, getChaptersOfBook