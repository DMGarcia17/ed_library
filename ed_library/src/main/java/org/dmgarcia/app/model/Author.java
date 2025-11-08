package org.dmgarcia.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "author", schema = "lb")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_author")
    private Integer idAuthor;

    @Column(name = "author", length = 50)
    private String author;

    @Column(name = "nationality", length = 20)
    private String nationality;

    @Column(name = "biography", length = 500)
    private String biography;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Author() {
    }

    public Author(String author, String nationality, String biography) {
        this.author = author;
        this.nationality = nationality;
        this.biography = biography;
    }

    public Integer getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(Integer idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
