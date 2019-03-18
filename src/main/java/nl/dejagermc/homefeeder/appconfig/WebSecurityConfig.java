package nl.dejagermc.homefeeder.appconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser("google")
                    .password(encoder.encode("mn4dH8BummKGqs84"))
                    .roles("GOOGLE")
                .and()
                .withUser("maxhunt")
                    .password(encoder.encode("154411mc"))
                    .roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/dialogflow/**").hasRole("GOOGLE")
                    .antMatchers("/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .httpBasic()
                .and()
                    .csrf().disable();
    }
}