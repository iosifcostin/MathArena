package iosifcostin.MathArena.controller.user;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import iosifcostin.MathArena.Service.CropImage;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.StaticVars.StaticVars;
import iosifcostin.MathArena.dto.PictureDto;
import iosifcostin.MathArena.model.Role;
import iosifcostin.MathArena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("/user")
public class ProfController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private UserService userService;
    private AmazonS3 s3client;

    @Autowired

    public ProfController(OAuth2AuthorizedClientService authorizedClientService, UserService userService, AmazonS3 s3client) {
        this.authorizedClientService = authorizedClientService;
        this.userService = userService;
        this.s3client = s3client;
    }



    @GetMapping("/profile")
    public String userIndex( Model model, Authentication authentication, HttpSession session) {


        User user = userService.findByEmail(authentication.getName());
        user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);

        if (user.getMathProblems().size() != 0)
            model.addAttribute("userSolvedProblems", user.getMathProblems());


        model.addAttribute("picture", user.getProfilePicturePath());
        model.addAttribute("problemsSize", StaticVars.problemsSize);
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


        model.addAttribute("picture", user.getProfilePicturePath());
        model.addAttribute("name", auth.getName());
        model.addAttribute("problemsSize", StaticVars.problemsSize);
        model.addAttribute("profile", true);
        session.setAttribute("userType", "googleUser");
        model.addAttribute("picUpload", new PictureDto());
        model.addAttribute("user", user);

        return "user/profile";
    }

    @PostMapping(value = "/newPicture")
    public String submitPictureUpload(@RequestParam MultipartFile file,
                                      @ModelAttribute("picture") @Valid final PictureDto picture,
                                      Model model, HttpSession session,
                                      HttpServletRequest request,
                                      Authentication authentication) {

        String pictureName = "img" + System.currentTimeMillis() + ".png";

        User user = new User();
        if (session.getAttribute("userType") == "normalUser")
            user = userService.findByEmail(authentication.getName());
        else if (session.getAttribute("userType") == "googleUser")
            user = userService.findByGoogleAuthId(authentication.getName());

        BufferedImage squareImage;
        CropImage cropImage = new CropImage();

        try {
            squareImage = cropImage.cropImageSquare(file.getBytes());
            uploadBufferedImageToServer(squareImage, pictureName, "png", user.getProfilePicturePath());


        } catch (IOException e) {
            e.printStackTrace();
        }

        userService.setProfilePicture(user.getId(), "https://matharena.s3.eu-central-1.amazonaws.com/" + pictureName);

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

    private void uploadBufferedImageToServer(BufferedImage image, String fileName, String imageType, String oldPicture) {

         final String bucketName = "matharena";

        // for heroku
//        String bucketName = System.getenv("S3_BUCKET_NAME");

        if (oldPicture != null) {
            s3client.deleteObject(bucketName,oldPicture.replace("https://matharena.s3.eu-central-1.amazonaws.com/",""));
        }

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = outstream.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/" + imageType);
        meta.setContentLength(buffer.length);

        s3client.putObject(new PutObjectRequest(bucketName, fileName, is, meta).withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
