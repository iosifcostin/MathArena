package iosifcostin.MathArena.controller.user;

import iosifcostin.MathArena.Service.CropImage;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.StaticVars.StaticVars;
import iosifcostin.MathArena.dto.PictureDto;
import iosifcostin.MathArena.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class ProfController {

    private UserService userService;

    public ProfController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String userIndex(Model model, Authentication authentication, HttpSession session) {


        User user = userService.findByEmail(authentication.getName());
        user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);

        if (user.getMathProblems().size() != 0)
            model.addAttribute("userSolvedProblems", user.getMathProblems());


        model.addAttribute("picture", user.getProfilePicturePath());
        model.addAttribute("problemsSize", StaticVars.problemsSize);
        model.addAttribute("user", user);
        model.addAttribute("profile", true);
        model.addAttribute("picUpload", new PictureDto());
        return "user/profile";
    }


    @GetMapping("/oauth/profile")
    public String userIndexOauth(Model model, OAuth2AuthenticationToken auth, HttpSession session) {

        User user = userService.findByClientAuthId(auth.getName());

        if (user != null)
            user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);

        if (user == null) {
            user = userService.getUserAttributes(auth);
            session.setAttribute("mail", user.getEmail());
            user.setRoles(userService.setUserRole());
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
        model.addAttribute("picUpload", new PictureDto());
        model.addAttribute("user", user);

        return "user/profile";
    }

    @PostMapping(value = "/newPicture")
    public String submitPictureUpload(@RequestParam MultipartFile file,
                                      @ModelAttribute("picture") @Valid final PictureDto picture,
                                      Model model, HttpSession session,
                                      HttpServletRequest request, Authentication authentication) {

        String pictureName = "img" + System.currentTimeMillis() + ".png";
        boolean isOauth = userService.isOauth(authentication);

        User user;
        if (isOauth)
            user = userService.findByClientAuthId(authentication.getName());
        else
            user = userService.findByEmail(authentication.getName());


        BufferedImage squareImage;
        CropImage cropImage = new CropImage();

        try {
            squareImage = cropImage.cropImageSquare(file.getBytes());
            userService.uploadBufferedImageToServer(squareImage, pictureName,  user.getProfilePicturePath());


        } catch (IOException e) {
            e.printStackTrace();
        }

        userService.setProfilePicture(user.getId(), "https://matharena.s3.eu-central-1.amazonaws.com/" + pictureName);

        if (isOauth)
            return "redirect:/user/oauth/profile";
        else
            return "redirect:/user/profile";

    }

}
