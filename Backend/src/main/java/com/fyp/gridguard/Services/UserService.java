package com.fyp.gridguard.Services;


import com.fyp.gridguard.Models.Users;
import com.fyp.gridguard.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Users userRegistration(Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }


    public Users findByEmail(String email) {
        return userRepo.findByEmail(email);
    }


}
