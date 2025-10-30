package com.friendfinder.repository;
import com.friendfinder.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM USERS",
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
    }

    public void save(User user) {
        jdbcTemplate.update("INSERT INTO USERS (name, email) VALUES (?, ?)",
                user.getName(), user.getEmail());
    }
}
