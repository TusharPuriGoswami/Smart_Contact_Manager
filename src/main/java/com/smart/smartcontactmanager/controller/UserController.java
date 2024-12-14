package com.smart.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.halper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

@Autowired
private UserRepository userRepository;

@Autowired
private ContactRepository contactRepository;
    private String userName;

//method for addin common data to response
@ModelAttribute
public void addCommonData(Model model , Principal principal){
	String userName = principal.getName();
		System.out.println("UserName   " + userName);
		//get the user using username(Email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER" + user);
		model.addAttribute("user",user);


}

//DashBoard Home	
	@RequestMapping("/index")
	public String dashboard(org.springframework.ui.Model model , Principal principal) {
		model.addAttribute("title" ,"User Dashboard");
		return "normal/user_dashboard";	
	}

	//open add form handler
	@GetMapping("/add-contact")
    public String openAddContactForm(Model model, HttpSession session) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        // Retrieve message from session if present
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");  // Remove the message after retrieval
        }

        return "normal/add_contact_form";
    }



	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
								 @RequestParam("ProfileImage") MultipartFile file,
								 Principal principal, HttpSession session ) {
	
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			/* if(3>2){
				throw new Exception();
			} */
	
			// processing and uploading file
			if (file.isEmpty()) {
				System.out.println("Image is Empty");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
	
				// File save directory
				
				Path path = Paths.get(saveFile.getAbsolutePath(), File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	
				System.out.println("Image is uploaded!!!");
			}
	
			
			user.getContacts().add(contact);
			contact.setUser(user);
			this.userRepository.save(user);

			

			
			 session.setAttribute("message", new Message("Contact is Added Successfully!!", "success"));
			

			
			
		}catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !! please try again !!", "danger"));
		}
		
	
		return "normal/add_contact_form";
		
	}

	//show contact handler
	//per page = 5[n]
	//current page = [page]

	@GetMapping("show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page , Model m , Principal principal){

		m.addAttribute("title" , "User Contacts");

//Contact ki list bhejni h 

String userName = principal.getName();

User user = this.userRepository.getUserByUserName(userName);

//per page = 5[n]
	//current page = [page]

Pageable pageable = PageRequest.of(page, 6);

Page<Contact> contacts = this.contactRepository.findContacstByUser(user.getId(),pageable);

m.addAttribute("contacts" ,contacts);
m.addAttribute("currentPage" ,page);
m.addAttribute("totalPages" , contacts.getTotalPages());


		return "normal/show_contacts";
	}
	
	///showing perticular contact details

	@RequestMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid , Model model , Principal principal){
		System.out.println("cId "+cid);

		Optional<Contact> contactOptional =   this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();


		// user access by only user contacts
        String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		

		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact" , contact);
			model.addAttribute("title" , contact.getName());
		
		}

		return "normal/contact_detail";
	}

	//delete contact handler

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid , Model mdoel , HttpSession session , Principal principal){


	     Contact contact  = this.contactRepository.findById(cid).get();
		 

		 //check...

		 System.out.println("contact" + contact.getCid());

		// contact.setUser(null);

		//delect contact dataBase
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);

		this.userRepository.save(user);



		// this.contactRepository.delete(contact);

		 System.out.println("Deleted");

		 session.setAttribute("message", new Message("Contact deleted sucesfully...." , "success"));
		 

		 
		return "redirect:/user/show-contacts/0";
	}

	//open update form handler

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid , Model model ){

		model.addAttribute("title" , "Update Contact");

		Contact contact  = this.contactRepository.findById(cid).get();

		model.addAttribute("contact" , contact);
		return "normal/update_form";
	}

	//  update conatct handler
	
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updatehandler(@ModelAttribute Contact contact , @RequestParam("ProfileImage") MultipartFile file , Model model , HttpSession session , Principal principal){

		try {


			//old contact details
			Contact oldcontactDetail =  this.contactRepository.findById(contact.getCid()).get();

			


			//image

			if (!file.isEmpty()) {
				
				//file work 
				//Rewrite 

	//           delete old photo
	File deleteFile = new ClassPathResource("static/img").getFile();	
	File file1 = new File(deleteFile , oldcontactDetail.getImage());
	file1.delete();


   
	//           update new photo
	     File saveFile = new ClassPathResource("static/img").getFile();	
	     Path path = Paths.get(saveFile.getAbsolutePath(), File.separator+file.getOriginalFilename());
	     Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		 contact.setImage(file.getOriginalFilename());

			}
			else{
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepository.save(contact);

			
			session.setAttribute("message", new Message("Your contact is updated...", "success"));
			
		} catch (Exception e) {
		e.printStackTrace();
		session.setAttribute("message", new Message("Something went wrong during the update!", "danger"));
		}
		
		System.out.println("Contact Name" + contact.getName());
		System.out.println("Contact Id" +contact.getCid());

		return "redirect:/user/contact/" + contact.getCid();
	}

	//user profile handler


	@GetMapping("/profile")
	public String yourProfile(Model model){
		model.addAttribute("title" , "Profile Page"); 

		return "normal/profile";

	}

	//open setting handler
	@GetMapping("/settings")
	public String openSetting(Model model){
		model.addAttribute("title" , "Setting");

		return "normal/settings";
	}

	//change password handler
	@PostMapping("change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword , @RequestParam("newPassword") String newPassword , Principal principal , HttpSession session){

		System.out.println("old password = " + oldPassword);
		System.out.println("new password = " + newPassword);


		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());

		if (oldPassword.equals(newPassword)) {
			session.setAttribute("message", new Message("Please enter a new password different from the old one!", "danger"));
			return "redirect:/user/settings";
		}


		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){

			//changer the password

			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);

			session.setAttribute("message" , new Message("your password Sucessfully changed..." , "success"));

		}
		
		
		else{
			//error...
			session.setAttribute("message" , new Message("please enter currect password !!" , "danger"));

			return "redirect:/user/settings";

		}

		return "redirect:/user/index";

	}

	

	}
