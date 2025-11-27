package com.friendfinder.config;

import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.repository.InterestRepository;
import com.friendfinder.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class InterestSeeder implements CommandLineRunner {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    public InterestSeeder(InterestRepository interestRepository, UserRepository userRepository) {
        this.interestRepository = interestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<String> seedNames = List.of(
                "Sports", "Music", "Gaming",
                "Boardgames", "Coding", "Fitness", "Reading"
        );

        List<Interest> allInterests = (List<Interest>) interestRepository.findAll();

        List<Interest> toRemove = allInterests.stream()
                .filter(i -> !seedNames.contains(i.getName()))
                .toList();

        List<User> users = (List<User>) userRepository.findAll();
        for (User user : users) {
            user.getInterests().removeAll(toRemove);
        }
        userRepository.saveAll(users);

        interestRepository.deleteAll(toRemove);

        for (String name : seedNames) {
            if (!interestRepository.existsByName(name)) {
                Interest interest = new Interest();
                interest.setName(name);
                interestRepository.save(interest);
            }
        }

        System.out.println("Interests synchronized with seed list");
    }

    private Interest createInterest(String name) {
        Interest interest = new Interest();
        interest.setName(name);
        return interest;
    }
}