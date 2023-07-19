package com.gunam.app.Entity;

public class Admin{
    private Long admin_id;
    private String name;
    private String surname;
    private String email;
    private String password;

    public Admin() {

    }

    public Admin(Long admin_id, String name, String surname, String email, String password) {
        this.admin_id = admin_id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

    public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
