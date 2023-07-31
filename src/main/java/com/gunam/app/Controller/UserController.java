package com.gunam.app.Controller;

import com.gunam.app.Entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class UserController {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/users")
    public Collection<User> users() {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                (rs, rowNum) -> {
                    User user = new User();
                    user.setUser_id(rs.getLong("user_id"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setCompany(rs.getString("company"));
                    user.setPhone(rs.getString("phone"));
                    user.setPosition(rs.getString("position"));
                    return user;
                }
        );

        System.out.println("---ALL USERS---");
        for (User user : users) {
            System.out.println(user.getName());
        }

        return users;
    }


    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        String sql = "SELECT * FROM \"user\" WHERE user_id = ?";
        Object[] params = {id};

        List<User> users = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setUser_id(rs.getLong("user_id"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setCompany(rs.getString("company"));
                    user.setPhone(rs.getString("phone"));
                    user.setPosition(rs.getString("position"));
                    return user;
                }
        );

        if (users.isEmpty()) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }

        System.out.println("---GOT---");

        return users.get(0);
    }

    @GetMapping("/user/login")
    public User userLogin(@RequestParam String email, @RequestParam String password) {
        String sql = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";
        Object[] params = {email, password};
        System.out.println(email + " " + password);

        List<User> users = jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setUser_id(rs.getLong("user_id"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setCompany(rs.getString("company"));
                    user.setPhone(rs.getString("phone"));
                    user.setPosition(rs.getString("position"));
                    return user;
                }
        );

        if (users.isEmpty()) {
            throw new NoSuchElementException("User not found with the provided email and password");
        }
        System.out.println("---LOGGED IN---");
        System.out.println(users.get(0).getName());
        return users.get(0);
    }

    @PostMapping("/user")
    void createUser(@Valid @RequestBody String jsonPayload) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);

        String name = jsonNode.get("name").asText();
        String surname = jsonNode.get("surname").asText();
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();
        String company = jsonNode.get("company").asText();
        String position = jsonNode.get("position").asText();
        String phone = jsonNode.get("phone").asText();


        // Perform further operations with the User object
        System.out.println("---CREATED---");
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Company: " + company);
        System.out.println("Position: " + position);
        System.out.println("Phone: " + phone);

        jdbcTemplate.update(
                "INSERT INTO \"user\" (name , surname, email, password, company, position, phone) VALUES (?, ?, ?, ?, ?, ?, ?)",
                name,
                surname,
                email,
                password,
                company,
                position,
                phone
        );
    }
    @PutMapping("/user/update/{id}")
    void updateUser(@PathVariable Long id, @Valid @RequestBody String jsonPayload) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);

        String name = jsonNode.get("name").asText();
        String surname = jsonNode.get("surname").asText();
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();
        String company = jsonNode.get("company").asText();
        String position = jsonNode.get("position").asText();
        String phone = jsonNode.get("phone").asText();


        // Perform further operations with the updated user information
        System.out.println("---UPDATED---");
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Company: " + company);
        System.out.println("Position: " + position);
        System.out.println("Phone: " + phone);

        jdbcTemplate.update(
                "UPDATE \"user\" SET name = ?, surname = ?, email = ?, password = ?, company = ?, position = ?, phone = ? WHERE user_id = ?",
                name,
                surname,
                email,
                password,
                company,
                position,
                phone,
                id
        );
    }
    @PutMapping("/user/remove/{id}")
    void removeUser(@PathVariable Long id) {
        String sql = "DELETE FROM \"user\" WHERE user_id = ?";
        Object[] params = {id};

        int affectedRows = jdbcTemplate.update(sql, params);

        if (affectedRows == 0) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }
    }

}
