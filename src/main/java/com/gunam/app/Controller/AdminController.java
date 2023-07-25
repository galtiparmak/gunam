package com.gunam.app.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunam.app.Entity.Admin;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class AdminController {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/admins")
    Collection<Admin> admins() {
        List<Admin> admins = jdbcTemplate.query(
                "SELECT * FROM \"admin\"",
                (rs, rowNum) -> {
                    Admin admin = new Admin();
                    admin.setAdmin_id(rs.getLong("admin_id"));
                    admin.setName(rs.getString("name"));
                    admin.setSurname(rs.getString("surname"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
        );

        System.out.println("---ALL ADMINS---");
        for (Admin admin : admins) {
            System.out.println(admin.getName());
        }

        return admins;
    }


    @GetMapping("/admins/{id}")
    public Admin getAdmin(@PathVariable Long id) {
        String sql = "SELECT * FROM \"admin\" WHERE admin_id = ?";
        Object[] params = {id};

        List<Admin> admins = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    Admin admin = new Admin();
                    admin.setAdmin_id(rs.getLong("admin_id"));
                    admin.setName(rs.getString("name"));
                    admin.setSurname(rs.getString("surname"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
        );

        if (admins.isEmpty()) {
            throw new NoSuchElementException("Admin not found with ID: " + id);
        }

        System.out.println("---GOT---");

        return admins.get(0);
    }

    @GetMapping("/admin/login")
    public Admin adminLogin(@RequestParam String email, @RequestParam String password) {
        String sql = "SELECT * FROM \"admin\" WHERE email = ? AND password = ?";
        Object[] params = {email, password};

        List<Admin> admins = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    Admin admin = new Admin();
                    admin.setAdmin_id(rs.getLong("admin_id"));
                    admin.setName(rs.getString("name"));
                    admin.setSurname(rs.getString("surname"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
        );

        if (admins.isEmpty()) {
            throw new NoSuchElementException("Admin not found with the provided email and password");
        }
        System.out.println("---LOGGED IN---");

        return admins.get(0);
    }

    @PostMapping("/admin")
    void createAdmin(@Valid @RequestBody String jsonPayload) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);

        String name = jsonNode.get("name").asText();
        String surname = jsonNode.get("surname").asText();
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();


        // Perform further operations with the User object
        System.out.println("---CREATED---");
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        jdbcTemplate.update(
                "INSERT INTO \"admin\" (name , surname, email, password) VALUES (?, ?, ?, ?)",
                name,
                surname,
                email,
                password
        );
    }
    @PutMapping("/update/admin/{id}")
    void updateAdmin(@PathVariable Long id, @Valid @RequestBody String jsonPayload) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);

        String name = jsonNode.get("name").asText();
        String surname = jsonNode.get("surname").asText();
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();


        // Perform further operations with the updated user information
        System.out.println("---UPDATED---");
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        jdbcTemplate.update(
                "UPDATE \"admin\" SET name = ?, surname = ?, email = ?, password = ? WHERE admin_id = ?",
                name,
                surname,
                email,
                password,
                id
        );
    }
    @PutMapping("/remove/admin/{id}")
    void removeAdmin(@PathVariable Long id) {
        String sql = "DELETE FROM \"admin\" WHERE admin_id = ?";
        Object[] params = {id};

        int affectedRows = jdbcTemplate.update(sql, params);

        if (affectedRows == 0) {
            throw new NoSuchElementException("Admin not found with ID: " + id);
        }
    }
}
