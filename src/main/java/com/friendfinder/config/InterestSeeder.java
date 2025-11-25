package com.friendfinder.config;

import com.friendfinder.model.Interest;
import com.friendfinder.repository.InterestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class InterestSeeder implements CommandLineRunner {

    private final InterestRepository interestRepository;

    public InterestSeeder(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @Override
    public void run(String... args) {
        List<String> desiredInterests = List.of(
                "Sports", "Music", "Travel", "Gaming", "Boardgames",
                "Coding", "Fitness", "Reading"
        );

        for (String name : desiredInterests) {
            if (!interestRepository.existsByName(name)) {
                Interest interest = new Interest();
                interest.setName(name);
                interestRepository.save(interest);
            }
        }

        System.out.println("Ensured all default interests are present");

    }

    private Interest createInterest(String name) {
        Interest interest = new Interest();
        interest.setName(name);
        return interest;
    }
}