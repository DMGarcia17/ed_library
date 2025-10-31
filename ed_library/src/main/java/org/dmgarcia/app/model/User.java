package org.dmgarcia.app.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @Column(length = 50)
    private String username;

    @Column(name="first_name", length = 50)
    private String firstName;

    @Column(name="middle_name", length = 50)
    private String middleName;

    @Column(name="last_name", length = 50)
    private String lastName;

    @Column(name ="family_name", length = 50)
    private String familyName;

    @Column(name="password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name="last_password_update")
    private LocalDateTime lastUpdatePassword;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(String roleCode){
        return roles.stream().anyMatch(r->r.getCode().equalsIgnoreCase(roleCode));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getLastUpdatePassword() {
        return lastUpdatePassword;
    }

    public void setLastUpdatePassword(LocalDateTime lastUpdatePassword) {
        this.lastUpdatePassword = lastUpdatePassword;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
