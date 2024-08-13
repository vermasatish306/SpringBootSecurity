package com.app.security.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.security.entity.Customer;
import com.app.security.rep.CustomerRepository;
@Service
public class CustomerServices implements UserDetailsService {
	@Autowired
	private CustomerRepository rep;
	@Override
	public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
	    Customer customer = rep.findByUname(uname);
	    if (customer == null) {
	        throw new UsernameNotFoundException("User not found with username: " + uname);
	    }
	    return new User(customer.getUname(), customer.getPsw(), Collections.emptyList());
	}


}
