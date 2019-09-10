package iosifcostin.MathArena.controller.user;


import iosifcostin.MathArena.Service.CategoryService;
import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.Service.ProblemClassService;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.Service.searching.ProblemFinder;
import iosifcostin.MathArena.Service.searching.ProblemSearchErrorResponse;
import iosifcostin.MathArena.Service.searching.ProblemSearchParameters;
import iosifcostin.MathArena.Service.searching.ProblemSearchResult;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.Category;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.ProblemClass;
import iosifcostin.MathArena.model.User;
import iosifcostin.MathArena.paging.InitialPagingSizes;
import iosifcostin.MathArena.paging.Pager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class ProblemController {

    private MathProblemService mathProblemService;
    private UserService userService;

    public ProblemController(MathProblemService mathProblemService, UserService userService) {
        this.mathProblemService = mathProblemService;
        this.userService = userService;
    }

    @GetMapping("/problemDetails/{id}")
    public String userProblemDetails(@PathVariable Long id, Model model, Authentication authentication, HttpSession session) {

        MathProblem mathProblem = mathProblemService.findById(id);

        User user;
        if (userService.isOauth(authentication))
            user = userService.findByClientAuthId(authentication.getName());
        else
            user = userService.findByEmail(authentication.getName());

        if (mathProblem != null) {

            model.addAttribute("problemSolved", user.getMathProblems().contains(mathProblem));
            model.addAttribute("mathProblem", mathProblem);
            model.addAttribute("idTo" , id);
        }
        else{
            model.addAttribute("id" , id);
        }
        model.addAttribute("next", id+1);
        model.addAttribute("preview", id-1);
        model.addAttribute("page" , (int)session.getAttribute("page") + 1);
        return "user/problemDetails";
    }
}
