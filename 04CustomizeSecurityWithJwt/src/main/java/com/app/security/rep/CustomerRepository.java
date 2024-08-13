package com.app.security.rep;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.security.entity.Customer;
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	public Customer findByUname( String uname);
}
