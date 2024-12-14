package com.smart.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

     //pagination method....
     

     @Query("from Contact as c where c.user.id =:userId")
     //currentPage-page
     //contact per page - 5
     public Page<Contact> findContacstByUser(@Param("userId")int userId , Pageable pageable);

     //search method
     public List<Contact> findByNameContainingAndUser(String name , User user);
    
}
