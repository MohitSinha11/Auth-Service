package org.example.service;

import org.example.entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
//implements wala error hai
public class CustomUserDetails extends UserInfo , UserDetails {
    private String username;
    private String password;
    Collection<? extends GrantedAuthority> authorities;

}
