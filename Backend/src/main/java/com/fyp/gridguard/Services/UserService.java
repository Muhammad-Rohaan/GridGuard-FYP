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

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        Users user = userRepo.findByEmail(email);
        if (user == null || !bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepo.save(user);
        return true;
    }

}
