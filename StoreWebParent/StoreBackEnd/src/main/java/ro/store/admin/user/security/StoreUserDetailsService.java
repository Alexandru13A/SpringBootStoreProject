package ro.store.admin.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ro.store.admin.user.UserRepository;
import ro.store.common.entity.User;

@Component
public class StoreUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository repo;



  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = repo.getUserByEmail(email);

    if (user != null) {
      return new StoreUserDetails(user);
    }

    throw new UsernameNotFoundException("Could not find user with email: " + email);
  }

}