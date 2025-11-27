package com.friendfinder.controller;

import com.friendfinder.model.User;
import com.friendfinder.services.AuthenticatorService;
import com.friendfinder.services.FriendService;
import com.friendfinder.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired private FriendService friendService;
    @Autowired private UserRepository userRepo;

    @GetMapping("/add/{email}")
    public String sendRequest(@PathVariable String email, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        User sender = auth.user();
        User receiver = userRepo.findByEmail(email.toLowerCase());
        if (receiver != null && !receiver.getEmail().equals(sender.getEmail())) {
            friendService.sendRequest(sender, receiver);
        }
        return "redirect:/users/all";
    }

    @GetMapping("/requests")
    public String viewRequests(HttpSession session, Model model) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        model.addAttribute("requests", friendService.getPendingRequests(auth.user()));
        return "friend-requests";
    }

    @PostMapping("/accept/{id}")
    public String acceptRequest(@PathVariable Long id, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        friendService.acceptRequest(id);
        return "redirect:/friends/requests";
    }

    @PostMapping("/decline/{id}")
    public String declineRequest(@PathVariable Long id, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        friendService.declineRequest(id);
        return "redirect:/friends/requests";
    }

    @GetMapping("/list")
    public String viewFriends(Model model, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        User currentUser = auth.user();
        model.addAttribute("friends", friendService.getFriends(currentUser));
        return "friends-list";
    }

    @PostMapping("/remove/{email}")
    public String removeFriend(@PathVariable String email, HttpSession session) {
        var auth = (AuthenticatorService.Auth) session.getAttribute("auth");
        if (auth == null) return "redirect:/login";

        User currentUser = auth.user();
        User friend = userRepo.findByEmail(email.toLowerCase());

        if (friend != null) {
            friendService.removeFriend(currentUser, friend);
        }

        return "redirect:/friends/list";
    }

}
