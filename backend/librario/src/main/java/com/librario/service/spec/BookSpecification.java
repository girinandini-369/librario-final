package com.librario.service.spec;

import com.librario.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> hasGenre(String genre) {
        return (root, query, cb) -> genre == null ? null :
                cb.equal(root.get("genre"), genre);
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> author == null ? null :
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> hasPublisher(String publisher) {
        return (root, query, cb) -> publisher == null ? null :
                cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> title == null ? null :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> advancedSearch(String genre, String author, String publisher, String title) {
        return Specification.where(hasGenre(genre))
                .and(hasAuthor(author))
                .and(hasPublisher(publisher))
                .and(hasTitle(title));
    }
}
