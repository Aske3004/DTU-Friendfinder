package com.friendfinder.controller;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidPasswordException;
import com.friendfinder.model.User;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.FriendService;
import com.friendfinder.utils.Field;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppController {

    private final AuthenticatorService authenticatorService;
    private final FriendService friendService;


    public AppController(AuthenticatorService authenticatorService, FriendService friendService) {
        this.authenticatorService = authenticatorService;
        this.friendService = friendService;
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

        return "index";
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
