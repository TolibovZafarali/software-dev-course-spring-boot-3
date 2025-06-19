package com.example.springBoot2.controllers;

import com.example.springBoot2.Repositories.BookRepository;
import com.example.springBoot2.models.Book;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("")
    public String renderBooksHomePage() {
        List<Book> allBooks = bookRepository.findAll();
        StringBuilder booksList = new StringBuilder();
        for (Book book : allBooks) {
            booksList.append("<li><a href='/books/details/").append(book.getId()).append("'>").append(book.getName()).append(" - ").append(book.getAuthor()).append("</a></li>");
        }
        return """
                <html>
                <body>
                <h2>BOOKS</h2>
                <ul>
                """ +
                booksList +
                """
                </ul>
                <p><a href='/books/add'>Add</a> another book or <a href='/books/delete'>delete</a> one or more books.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/details/{bookId}")
    public String displayBookDetails(@PathVariable(value="bookId") int bookId) {
        Book currentBook = bookRepository.findById(bookId).orElse(null);
        if (currentBook != null) {
            return """
                    <html>
                    <body>
                    <h3>Book Details</h3>
                    """ +
                    "<p><b>ID:</b> " + bookId + "</p>" +
                    "<p><b>Name:</b> " + currentBook.getName() + "</p>" +
                    "<p><b>Author:</b> " + currentBook.getAuthor() + "</p>" +
                    "<p><b>Year:</b> " + currentBook.getYear() + "</p>" +
                    "<p><b>Pages:</b> " + currentBook.getPages() + "</p>" +
                    "<p><a href='/books/update/" + currentBook.getId() + "'>Update</a></p>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Book Details</h3>
                    <p>Book not found. <a href='/books'>Return to list of books.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/add")
    public String renderAddBookForm() {
        return """
                <html>
                <body>
                <form action='/books/add' method='POST'>
                <p>Enter the details of a book</p>
                <input type='text' name='name' placeholder='Name' />
                <input type='text' name='author' placeholder='Author' />
                <input type='number' name='year' placeholder='Year' />
                <input type='number' name='pages' placeholder='Pages' />
                <button type='submit'>Add</button>
                </form>
                </body>
                </html>
                """;
    }
    
    @PostMapping("/add")
    public String processAddBookForm(@RequestParam(value="name") String name, @RequestParam(value="author") String author, @RequestParam(value="year") int year, @RequestParam(value="pages") int pages) {
        Book newBook = new Book(name, author, year, pages);
        bookRepository.save(newBook);
        return """
                <html>
                <body>
                <h3>BOOK ADDED</h3>
                """ +
                "<p>You have successfully added " + name + " to the collection.</p>" +
                """
                <p><a href='/books/add'>Add</a> another book or view the <a href='/books'>updated list</a> of books.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/update/{bookId}")
    public String updateBookDetails(@PathVariable(value = "bookId") int bookId) {
        Book currentBook = bookRepository.findById(bookId).orElse(null);
        if (currentBook != null) {
            return """
                    <html>
                    <body>
                    <h3>Update Artwork</h3>
                    """ +
                    "<form action='/books/update/" + currentBook.getId() + "' method='POST'>" +
                    "<p>Update the details of a book:</p>" +
                    "<input type='text' name='name' value='" + currentBook.getName() + "' placeholder='Name' />" +
                    "<input type='text' name='author' value='" + currentBook.getAuthor() + "' placeholder='Author' />" +
                    "<input type='number' name='year' value='" + currentBook.getYear() + "' placeholder='Year' />" +
                    "<input type='number' name='pages' value='" + currentBook.getPages() + "' placeholder='Pages' />" +
                    "<button type='submit'>Update</button>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Book</h3>
                    <p>Book not found. <a href='/books'>Return to list of books.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @PostMapping("/update/{bookId}")
    public String processUpdateBookDetails(@PathVariable(value = "bookId") int bookId, @RequestParam(value = "name") String name, @RequestParam(value = "author") String author, @RequestParam(value = "year") int year, @RequestParam(value = "pages") int pages) {
        Book bookToUpdate = bookRepository.findById(bookId).orElse(null);

        if (bookToUpdate != null) {
            bookToUpdate.setName(name);
            bookToUpdate.setAuthor(author);
            bookToUpdate.setYear(year);
            bookToUpdate.setPages(pages);

            bookRepository.save(bookToUpdate);

            return """
                <html>
                <body>
                <h3>BOOK UPDATED</h3>
                """ +
                    "<p>You have successfully updated " + name + " to the collection.</p>" +
                    """
                    <p>View the <a href='/books'>updated list</a> of books.</p>
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Book</h3>
                    <p>Book not found. <a href='/books'>Return to list of books.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/delete")
    public String renderDeleteBookForm() {
        List<Book> allBooks = bookRepository.findAll();
        StringBuilder booksList = new StringBuilder();
        for (Book book : allBooks) {
            int currId = book.getId();
            booksList.append("<li><input id='").append(currId).append("' name='bookIds' type='checkbox' value='").append(currId).append("' />").append(book.getName()).append(" - ").append(book.getAuthor()).append("</li>");
        }
        return """
                <html>
                <body>
                <form action='/books/delete' method='POST'>
                <p>Select which books you wish to delete:</p>
                <ul>
                """ +
                booksList +
                """
                </ul>
                <button type='submit'>Submit</button>
                </form>
                </body>
                </html>
                """;
    }
    
    @PostMapping("/delete")
    public String ProcessDeleteBookForm(@RequestParam(value="bookIds") int[] bookIds) {
        for (int id : bookIds) {
            Book currBook = bookRepository.findById(id).orElse(null);
            if (currBook != null) {
                bookRepository.deleteById(id);

            }
        }
        String header = bookIds.length > 1 ? "BOOKS" : "BOOK";
        return """
                <html>
                <body>
                <h3>
                """ +
                header +
                """
                DELETED</h3>
                <p>Deletion successful.</p>
                <p>View the <a href='/books'>updated list</a> of books.</p>
                </body>
                </html>
                """;
    }
}
