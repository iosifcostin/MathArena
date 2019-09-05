package iosifcostin.MathArena.controller.user;

import iosifcostin.MathArena.Service.CropImage;
import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.Service.RoleService;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.StaticVars.StaticVars;
import iosifcostin.MathArena.controller.website.WebsiteNavController;
import iosifcostin.MathArena.dto.PictureDto;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.Role;
import iosifcostin.MathArena.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class ProfController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private UserService userService;
    private MathProblemService mathProblemService;
    RoleService roleService;

    public ProfController(OAuth2AuthorizedClientService authorizedClientService, UserService userService, MathProblemService mathProblemService) {
        this.authorizedClientService = authorizedClientService;
        this.userService = userService;
        this.mathProblemService = mathProblemService;
    }

    @GetMapping("/profile")
    public String userIndex(Model model, Authentication authentication, HttpSession session) {

        User user = userService.findByEmail(authentication.getName());
        user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);

        if (user.getMathProblems().size() != 0)
            model.addAttribute("userSolvedProblems", user.getMathProblems());

        model.addAttribute("problemsSize", StaticVars.problemsSize );
        model.addAttribute("picture", user.getProfilePicturePath());
        model.addAttribute("user", user);
        model.addAttribute("profile", true);
        session.setAttribute("userType", "normalUser");
        model.addAttribute("picUpload", new PictureDto());
        return "user/profile";
    }


    @GetMapping("/oauth/profile")
    public String userIndexOauth(Model model, OAuth2AuthenticationToken auth, HttpSession session) {

        User user = userService.findByGoogleAuthId(auth.getName());

        if (user != null)
        user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);

        if (user == null) {
            user = userAttributes(auth);
            session.setAttribute("mail", user.getEmail());
            user.setRoles((List<Role>) session.getAttribute("roles"));
            session.removeAttribute("roles");
            userService.save(user);
            model.addAttribute("welcome", "Bine ai venit pe MathArena ");
        }

        if (user.getMathProblems().size() != 0)
            model.addAttribute("userSolvedProblems", user.getMathProblems());

        model.addAttribute("name", auth.getName());
        model.addAttribute("picture", user.getProfilePicturePath());
        model.addAttribute("problemsSize", StaticVars.problemsSize );
        model.addAttribute("profile", true);
        session.setAttribute("userType", "googleUser");
        model.addAttribute("picUpload", new PictureDto());
        model.addAttribute("user" , user);

        return "user/profile";
    }

    @PostMapping(value = "/newPicture")
    public String submitPictureUpload(@RequestParam MultipartFile file,
                                      @ModelAttribute("picture") @Valid final PictureDto picture,
                                      Model model, HttpSession session,
                                      HttpServletRequest request,
                                      Authentication authentication) {

        String folderGeneratedImage = "src/main/resources/static/images/";
        String pictureName = "img" + System.currentTimeMillis()+".png";

        User user = new User();
        if (session.getAttribute("userType") == "normalUser")
            user = userService.findByEmail(authentication.getName());
        else if (session.getAttribute("userType") == "googleUser")
            user = userService.findByGoogleAuthId(authentication.getName());

        BufferedImage squareImage;
        CropImage cropImage = new CropImage();

        try {
            squareImage = cropImage.cropImageSquare(file.getBytes());
            File newFile = new File(folderGeneratedImage + pictureName);
            ImageIO.write(squareImage, "png", newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        userService.setProfilePicture(user.getId(),"/images/" + pictureName);


        if (session.getAttribute("userType") == "normalUser")
            return "redirect:/user/profile";
        else if (session.getAttribute("userType") == "googleUser")
            return "redirect:/user/oauth/profile";

        return null;
    }


    private User userAttributes(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient authorizedClient = authorizedClient(auth);
        Map userAttributes = Collections.emptyMap();
        User user = new User();
        String userInfoEndpointUri = authorizedClient.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {    // userInfoEndpointUri is optional for OIDC Clients
            userAttributes = WebClient.builder()
                    .filter(oauth2Credentials(authorizedClient))
                    .build()
                    .get()
                    .uri(userInfoEndpointUri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        }
        assert userAttributes != null;
        user.setFirstName((String) userAttributes.get("given_name"));
        user.setLastName((String) userAttributes.get("family_name"));
        user.setGoogleAuthId(auth.getName());
        user.setEmail((String) userAttributes.get("email"));
        user.setEnabled(true);
        user.setProfilePicturePath((String) userAttributes.get("picture"));

        return user;
    }

    private OAuth2AuthorizedClient authorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

    }

    private ExchangeFilterFunction oauth2Credentials(OAuth2AuthorizedClient authorizedClient) {
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                            .build();
                    return Mono.just(authorizedRequest);
                });
    }
}
