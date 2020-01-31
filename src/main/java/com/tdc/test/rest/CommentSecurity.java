package com.tdc.test.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.util.Collection;
import java.util.Set;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class CommentSecurity extends WebSecurityConfigurerAdapter {

    private static final Collection<String> USERS = Set.of(
            "user1",
            "user2",
            "user3",
            "user4",
            "user5"
    );

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        var configurer = auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance());
        USERS.forEach(user -> addUser(configurer, user));
    }

    private void addUser(UserDetailsManagerConfigurer configurer, String user) {
        configurer.withUser(user).password("password").authorities("ROLE_USER");
    }
}