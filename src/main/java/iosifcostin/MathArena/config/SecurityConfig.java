package iosifcostin.MathArena.config;

import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.Service.userDetails.UserDetailsServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    //Beans
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

//    @Bean
//    public CustomLogoutHandler logoutHandler() {
//        return new CustomLogoutHandler(userService);
//    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

//    @Bean
//    public RememberMeServices rememberMeServices() {
//        return new CustomRememberMeServices("theKey",
//                userDetailsServiceImpl, new InMemoryTokenRepositoryImpl());
//    }

    //Override methods
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                authorizeRequests()
                .antMatchers("/adminAssets/**", "/adminFragments/**", "/userFragments/**",
                        "/userAssets/**", "/websiteAssets/**", "/images/**","/problemsImages/**",
                        "/submit-registration", "/register","/top","/index","/problems","/checkAnswer","/getProblem",
                        "/login", "/", "/login-error", "/forgot-password").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/perform-login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/default", true)
                .failureUrl("/login-error")
                .and()
                .oauth2Login()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/user/oauth/profile", true)
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                .and()
                .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
//                .addLogoutHandler(logoutHandler())
                .permitAll();
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .maximumSessions(1)
//                .expiredUrl("/")
//                .and()
//                .invalidSessionUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }


}
