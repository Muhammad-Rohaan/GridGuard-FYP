package com.fyp.gridguard.Services;


import com.fyp.gridguard.Models.UserPrincipal;
import com.fyp.gridguard.Models.Users;
import com.fyp.gridguard.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SubstationUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
//        Users user = userRepo.findByFullName(userEmail);
        Users user = userRepo.findByEmail(userEmail);

        if (user == null) {
//            System.out.println("User with Email: "+userEmail+" not found");
            throw new UsernameNotFoundException("User Not Found");
        }
//        System.out.println(user.getEmail());
//        System.out.println(user.getFullName());
        return new UserPrincipal(user);
    }


}
