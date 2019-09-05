package iosifcostin.MathArena.controller.website;

import iosifcostin.MathArena.Service.RoleService;
import iosifcostin.MathArena.Service.UserService;
import org.springframework.context.annotation.Role;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("")
public class LoginController {

    private UserService userService;
    private RoleService roleService;

    private static String authorizationRequestBaseUri
            = "oauth2/authorization";
    private Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();

    private final ClientRegistrationRepository clientRegistrationRepository;

    public LoginController(UserService userService, ClientRegistrationRepository clientRegistrationRepository,RoleService roleService) {
        this.userService = userService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.roleService = roleService;
    }

    // Login form with error
    @GetMapping("/login-error")
    public String loginError(Model model, HttpSession session) {

        model.addAttribute("loginError", true);

        return "login";
    }

    @GetMapping(value = "/login")
    public String login(HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {

            /* The user is logged in :) */
            return "redirect:/default";
        }
        session.setAttribute("roles", Collections.singletonList(roleService.findByName("ROLE_USER")));
        return "login";
    }


    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {

        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "login";
    }

}
