package com.friendfinder.controller;

import com.friendfinder.exceptions.InvalidEmailException;
import com.friendfinder.exceptions.InvalidNameException;
import com.friendfinder.exceptions.InvalidPasswordException;
import com.friendfinder.model.User;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.UserService;
import com.friendfinder.utils.Field;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticatorService authenticatorService;

    @GetMapping("/create-user")
    public String createUser(Model model) {
        model.addAttribute("name", new Field("Text", "name", "Name", null, null));
        model.addAttribute("email", new Field("Email", "email", "Email", null, null));
        model.addAttribute("password", new Field("Password", "password", "Password", null, null));

        return "create-user";
    }

    @PostMapping("/create-user")
    public String postCreateUser(@ModelAttribute("user") User user, HttpSession session, Model model) {
        String nameMessage = "";
        String emailMessage = "";
        String passwordMessage = "";

        try {
            String rawPassword = user.getPassword();
            authenticatorService.register(user);
            AuthenticatorService.Auth auth = authenticatorService.authenticate(user.getEmail(), rawPassword);
            session.setAttribute("auth", auth);
            return "redirect:/";
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            model.addAttribute("error", e.getMessage());
        } catch (InvalidEmailException e) {
            System.err.println(e.getMessage());
            emailMessage = e.getMessage();
        } catch (InvalidNameException e) {
            System.err.println(e.getMessage());
            nameMessage = e.getMessage();
        } catch (InvalidPasswordException e) {
            System.err.println(e.getMessage());
            passwordMessage = e.getMessage();
        }

        model.addAttribute("name", new Field("Text", "name", "Name", user.getName(), nameMessage));
        model.addAttribute("email", new Field("Email", "email", "Email", user.getEmail(), emailMessage));
        model.addAttribute("password", new Field("Password", "password", "Password", null, passwordMessage));

        return "create-user";
    }
}
