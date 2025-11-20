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
            session.setAttribute("name", user.getName());
            session.setAttribute("user", user);
            session.setAttribute("email", user.getEmail());
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
    @GetMapping("/all")
    public String listUsers(Model model, HttpSession session) {
        var auth = session.getAttribute("auth");
        if (auth == null) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.findAllUsers());
        return "users";
    }

    @GetMapping("/user-profile")
    public String userprofile(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        model.addAttribute("name", new Field("Text", "name", (String) session.getAttribute("name"), null, null));
        model.addAttribute("email", new Field("Email", "email", (String) session.getAttribute("email"), null, null));
        model.addAttribute("password", new Field("Password", "password", "Password", null, null));

        return "user-profile";
    }

    @PostMapping("/update-name")
    public String updateName(@ModelAttribute("user") User user, HttpSession session, Model model) {
        String nameMessage = "";
        try {
            var user1 = (User) session.getAttribute("user");
            String name = user.getName();
            userService.updateUserName(name, user1.getEmail());
            session.setAttribute("name", name);
        }  catch (NullPointerException e) {
            System.err.println(e.getMessage());
        } catch (InvalidNameException e) {
            System.err.println(e.getMessage());
            nameMessage = e.getMessage();
        }
        model.addAttribute("name", new Field("Text", "name", "Name", user.getName(), nameMessage));

        return "redirect:/users/user-profile";
    }

    @PostMapping("/update-email")
    public String updateEmail(@ModelAttribute("user") User user, HttpSession session, Model model) {
        String nameMessage = "";
        String emailMessage = "";
        try {
            var user1 = (User) session.getAttribute("user");
            String newemail = user.getEmail();
            userService.updateUserEmail(newemail, user1.getEmail());
            session.setAttribute("email", newemail);
        }  catch (NullPointerException e) {
            System.err.println(e.getMessage());
        } catch (InvalidEmailException e) {
            System.err.println(e.getMessage());
            emailMessage = e.getMessage();
        }
        model.addAttribute("email", new Field("Email", "email", "Email", user.getEmail(), emailMessage));

        return "redirect:/users/user-profile";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@ModelAttribute("user") User user, HttpSession session, Model model) {
        try {
            User user1 = (User) session.getAttribute("user");
            userService.deleteUser(user1.getEmail());
            session.invalidate();
            return "redirect:/login";
        }   catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
        return "redirect:/users/user-profile";
    }
}
