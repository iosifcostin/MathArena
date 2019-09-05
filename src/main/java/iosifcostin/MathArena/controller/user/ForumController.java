package iosifcostin.MathArena.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class ForumController {

    @GetMapping("/forum")
    public String userForum(Model model) {
        model.addAttribute("forum", true);
        return "user/forum";
    }

}
