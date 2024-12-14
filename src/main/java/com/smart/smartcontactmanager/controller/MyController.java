package com.smart.smartcontactmanager.controller;

//import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.halper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MyController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model m) {
    	m.addAttribute("title","home - Smart Contact Manager");
        return "home";
    } 

    @RequestMapping("/about")
    public String about(Model m) {
    	m.addAttribute("title","about - Smart Contact Manager");
        return "about";
    }

    
	/*
	 * @RequestMapping("/signup") public String signup(Model m) {
	 * m.addAttribute("title","Register - Smart Contact Manager");
	 * m.addAttribute("user", new User()); return "signup"; }
	 */
    


    @GetMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());

        // Retrieve message from session if present
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");  // Remove message after retrieval
        }

        return "signup";
    }
   
    // this is register handler
    
   @RequestMapping(value = "/do_register", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user , BindingResult result1, @RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model model ,HttpSession session) {
        
try {
    

    
    if (!agreement) {
        
        throw new Exception("You have not agreed the terms and Conditions");
    }

    /* if (result1.hasErrors()) {
//        System.out.println("ERROR" + result1.toString());
        model.addAttribute("user", user);
        return "redirect:/signup";
    } */


    if (result1.hasErrors()) {
        model.addAttribute("user", user);
        return "signup"; // Don't use redirect here
    }


    user.setRole("ROLE_USER");
    user.setEnabled(true);
    user.setImageUrl("default.png");
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    


    System.out.println("terms and Conditions =  " + agreement);
    System.out.println("User  " + user);

     this.userRepository.save(user);

    model.addAttribute("user", new User());
    session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
    return "redirect:/signup";


}
catch (DataIntegrityViolationException e) {
    e.printStackTrace();
    model.addAttribute("user", user);
    session.setAttribute("message", new Message("Email already exists!", "alert-danger"));
    return "redirect:/signup";
}

catch (Exception e) {
    e.printStackTrace();
    model.addAttribute("user", user);
    session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
    System.out.println("Session Message: " + session.getAttribute("message"));
    return "redirect:/signup";  
}    
}


@GetMapping("/signin")
public String customLogin(Model model){
    model.addAttribute("title" , "Login page");
    return "login";
}


}