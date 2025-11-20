package com.friendfinder.model;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.exceptions.InvalidPasswordException;
import jakarta.persistence.*;
import java.util.List;
@Entity @Table(name = "interest_table")
@JsonIdentityInfo(
        scope = Interest.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id" )
public class Interest {
    @Id
    @Column(name = "interest_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "interests")
    private List<User> users;

    public Long getInterestId() {
        return id;
    }

    public void setInterestId(Long id) {
        this.id = id;
    }

    public String getInterestName() {
        return name;
    }

    public void setInterestName(String name) {
        this.name = name;
    }
}

