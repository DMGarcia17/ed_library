package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.Book;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookRepository extends BaseRepository {

    public Optional<Book> find(Integer id) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            TypedQuery<Book> q = em.createQuery("SELECT c FROM Book c " + "LEFT JOIN FETCH c.idCategory LEFT JOIN FETCH c.idAuthor WHERE c.id = :id", Book.class);
            q.setParameter("id", id);

            Book cat;
            try {
                cat = q.getSingleResult();
            } catch (NoResultException e) {
                cat = null;
            }

            return Optional.ofNullable(cat);
        }
    }

    public List<Book> listActive() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return em.createQuery("select r from Book r LEFT JOIN FETCH r.idCategory LEFT JOIN FETCH r.idAuthor where r.deletedAt is null order by r.id", Book.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Book save(Book r) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        return inTx(em, e -> e.merge(r));
    }

    public void softDelete(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            Book r = e.find(Book.class, code);
            if (r != null && r.getDeletedAt() == null) {
                r.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }

    public Optional<Book> findByIsbn(String isbn) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            TypedQuery<Book> q = em.createQuery("SELECT c FROM Book c " + "LEFT JOIN FETCH c.idCategory LEFT JOIN FETCH c.idAuthor WHERE c.isbn = :isbn", Book.class);
            q.setParameter("isbn", isbn);

            Book book;
            try {
                book = q.getSingleResult();
            } catch (NoResultException e) {
                book = null;
            }

            return Optional.ofNullable(book);
        }
    }
}