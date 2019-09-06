package iosifcostin.MathArena.controller.website;

import iosifcostin.MathArena.Service.EmailService;
import iosifcostin.MathArena.Service.RoleService;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;


@Controller
@RequestMapping("")
public class RegisterController {
    private UserService userService;
    private EmailService emailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private AuthenticationManager authenticationManager;
    RoleService roleService;


    public RegisterController(UserService userService, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, RoleService roleService) {
        this.userService = userService;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
    }

    @PostMapping(value = "/submit-registration")
    public ModelAndView saveUser(ModelAndView modelAndView,
                                 @ModelAttribute("user") @Valid final User user,
                                 BindingResult bindingResult, HttpSession session,
                                 HttpServletRequest request, Errors errors) throws ServletException {

        User emailExists = userService.findByEmail(user.getEmail());

        user.setProfilePicturePath("/images/no-image.jpg");

        if (emailExists != null) {
            modelAndView.setViewName("register");
            bindingResult.rejectValue("email", "alreadyRegisteredEmail",
                    "Acest email este deja inregistrat");
        }

        if (!user.getPassword().equals(user.getMatchingPassword())){
            modelAndView.setViewName("register");
            bindingResult.rejectValue("matchingPassword", "matchingPassword",
                    "Parolele nu potrivesc");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else {

            user.setEnabled(true);
            user.setRoles(Collections.singletonList(roleService.findByName("ROLE_USER")));
            String password = user.getPassword();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userService.save(user);

            /*String appUrl = request.getScheme() + "://" + request.getServerName();

            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setSubject("Registration Confirmation");
            registrationEmail.setText("Please confirm the registration");
            registrationEmail.setFrom("email@email.com");

            emailService.sendEmail(registrationEmail);*/

            session.setAttribute("emailMessage", "Un email de confirmare a fost trimis la "
                    + user.getEmail());

            request.login(user.getEmail(), password);

            modelAndView.setViewName("redirect:/user/profile");
        }

        return modelAndView;
    }

}

