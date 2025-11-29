package com.friendfinder.controller;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidPasswordException;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.FriendService;
import com.friendfinder.services.UserService;
import com.friendfinder.utils.Field;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppController {

    private final AuthenticatorService authenticatorService;
    private final FriendService friendService;

    private final UserService userService;
    private final UserRepository userRepository;


    public AppController(AuthenticatorService authenticatorService, FriendService friendService, UserService userService, UserRepository userRepository) {
        this.authenticatorService = authenticatorService;
        this.friendService = friendService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (session.getAttribute("auth") == null) {
            return "redirect:/login";
        }

        // Get current user from session
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        User currentUser = auth.user();

        // Get user's friends and add to model
        var friends = friendService.getFriends(currentUser);
        model.addAttribute("friends", friends);

        var potentialFriends = userService.findPotentialFriends(currentUser.getEmail());
        if (potentialFriends.isEmpty()) {
            model.addAttribute("firstPotential", null);
        }
        else {
            model.addAttribute("firstPotential", potentialFriends.get(0));
        }

        return "index";
    }

    @PostMapping("/like")
    public String like(@RequestParam String email, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        User sender = auth.user();
        User receiver = userRepository.findByEmail(email.toLowerCase());
        System.out.println("Sender: " + sender.getEmail() + ", receiver: " + email);
        if (receiver != null && !receiver.getEmail().equals(sender.getEmail())) {
            friendService.sendRequest(sender, receiver);
        }
        return "redirect:/";
    }



    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("email", new Field("Email", "email", "Email", null, null));
        model.addAttribute("password", new Field("Password", "password", "Password", null, null));
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute("user") User user, Model model, HttpSession session) {
        String emailMessage = "";
        String passwordMessage = "";

        try {
            AuthenticatorService.Auth auth = authenticatorService.authenticate(user.getEmail(), user.getPassword());
            session.setAttribute("auth", auth);
            session.setAttribute("user", user);
            User user1 = auth.user();
            session.setAttribute("name", user1.getName());
            session.setAttribute("email", user1.getEmail());
            return "redirect:/";
        } catch (InvalidEmailException e) {
            System.err.println(e.getMessage());
            emailMessage = e.getMessage();
        } catch (InvalidPasswordException e) {
            System.err.println(e.getMessage());
            passwordMessage = e.getMessage();
        }

        model.addAttribute("email", new Field("Email", "email", "Email", user.getEmail(), emailMessage));
        model.addAttribute("password", new Field("Password", "password", "Password", null, passwordMessage));
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
