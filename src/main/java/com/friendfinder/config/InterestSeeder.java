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
        if (interestRepository.count() == 0) {
            interestRepository.saveAll(List.of(
                    createInterest("Sports"),
                    createInterest("Music"),
                    createInterest("Travel"),
                    createInterest("Gaming")
            ));
            System.out.println("Seeded interests into DB");
        } else {
            System.out.println("Interests already exist, skipping seeding");
        }
    }

    private Interest createInterest(String name) {
        Interest interest = new Interest();
        interest.setName(name);
        return interest;
    }
}