
package com.company.bookhub.book.repository;

import com.company.bookhub.book.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookRepository
        extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {

    boolean existsByIsbn(String isbn);
    // Retrieves existing books matching multiple ISBNs.
    List<Book> findAllByIsbnIn(Collection<String> isbns);
    // Locks a book row for exclusive inventory updates.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    // Retrieves all books with their authors and categories for RAG indexing.
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllForRag();

    // Retrieves the complete BookHub catalog with related information.
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT b FROM Book b ORDER BY b.id")
    List<Book> findAllForCatalog();

    // Retrieves one book with its author and category for RAG indexing.
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdForRag(@Param("id") Long id);

    // Retrieves multiple current database records by their IDs,
    // including their authors and categories.
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT b FROM Book b WHERE b.id IN :ids")
    List<Book> findAllByIdInForRag(@Param("ids") Collection<Long> ids);

    // Finds a book by its exact title, ignoring capitalization.
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("""
            SELECT b
            FROM Book b
            WHERE LOWER(b.title) = LOWER(:title)
            """)
    Optional<Book> findByTitleForAi(@Param("title") String title);

    // Retrieves books belonging to a category, including author and category.
    @EntityGraph(attributePaths = {"author", "category"})
    List<Book> findAllByCategory_NameIgnoreCaseOrderByIdAsc(
            String categoryName
    );

    // Searches books using PostgreSQL full-text keyword search.
    @Query(value = """
            WITH search AS (
                SELECT to_tsquery(
                    'simple',
                    regexp_replace(
                        trim(
                            regexp_replace(
                                :query,
                                '[^[:alnum:]]+',
                                ' ',
                                'g'
                            )
                        ),
                        '[[:space:]]+',
                        ' | ',
                        'g'
                    )
                ) AS search_query
            )
            SELECT b.*
            FROM books b
            LEFT JOIN authors a ON a.id = b.author_id
            LEFT JOIN categories c ON c.id = b.category_id
            CROSS JOIN search s
            WHERE to_tsvector(
                'simple',
                concat_ws(
                    ' ',
                    COALESCE(b.title, ''),
                    COALESCE(b.isbn, ''),
                    COALESCE(a.name, ''),
                    COALESCE(a.biography, ''),
                    COALESCE(c.name, ''),
                    COALESCE(c.description, '')
                )
            ) @@ s.search_query
            ORDER BY
                ts_rank_cd(
                    to_tsvector(
                        'simple',
                        concat_ws(
                            ' ',
                            COALESCE(b.title, ''),
                            COALESCE(b.isbn, ''),
                            COALESCE(a.name, ''),
                            COALESCE(a.biography, ''),
                            COALESCE(c.name, ''),
                            COALESCE(c.description, '')
                        )
                    ),
                    s.search_query
                ) DESC,
                b.id ASC
            LIMIT 10
            """, nativeQuery = true)
    List<Book> searchByKeywordsForAi(@Param("query") String query);
}
