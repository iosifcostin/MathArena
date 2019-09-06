package iosifcostin.MathArena.controller.website;

import fmath.b.b.L;
import iosifcostin.MathArena.Service.*;
import iosifcostin.MathArena.Service.searching.ProblemFinder;
import iosifcostin.MathArena.Service.searching.ProblemSearchErrorResponse;
import iosifcostin.MathArena.Service.searching.ProblemSearchParameters;
import iosifcostin.MathArena.Service.searching.ProblemSearchResult;
import iosifcostin.MathArena.StaticVars.StaticVars;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.Category;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.ProblemClass;
import iosifcostin.MathArena.model.User;
import iosifcostin.MathArena.paging.InitialPagingSizes;
import iosifcostin.MathArena.paging.Pager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebsiteNavController {


    private MathMlToPng mathMlToPng;
    private MathProblemService mathProblemService;
    private ProblemClassService problemClassService;
    private CategoryService categoryService;
    private ProblemFinder problemFinder;
    private ProblemSearchErrorResponse problemSearchErrorResponse;
    private UserService userService;
    private RoleService roleService;


    public WebsiteNavController(MathMlToPng mathMlToPng, MathProblemService mathProblemService, ProblemClassService problemClassService, CategoryService categoryService, ProblemFinder problemFinder, ProblemSearchErrorResponse problemSearchErrorResponse, UserService userService, RoleService roleService) {
        this.mathMlToPng = mathMlToPng;
        this.mathProblemService = mathProblemService;
        this.problemClassService = problemClassService;
        this.categoryService = categoryService;
        this.problemFinder = problemFinder;
        this.problemSearchErrorResponse = problemSearchErrorResponse;
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping({"/", "/index"})
    public String index(Model model) {

        model.addAttribute("index", true);
        return "index";
    }

    @GetMapping("/top")
    public String userTop(Model model) {

        List<User> users = userService.findAll();
        Map<User, Integer> topUsers = new HashMap<>();
        users.forEach(user -> {
            if (user.getMathProblems().size() > 0) {
                user.setPercentDto((user.getMathProblems().size() * 100d) / StaticVars.problemsSize);
                topUsers.put(user, user.getMathProblems().size());
            }
        });

        final Map<User, Integer> sortedByValues = topUsers.entrySet()
                .stream()
                .sorted((Map.Entry.<User, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        model.addAttribute("problemsSize", StaticVars.problemsSize);
        model.addAttribute("topUsers", sortedByValues);
        model.addAttribute("top", true);
        return "top";
    }

    @RequestMapping("/problems")
    public ModelAndView problems(ModelAndView modelAndView, ProblemSearchParameters problemSearchParameters,
                                 Authentication authentication, HttpServletRequest request, HttpSession session) {

        int selectedPageSize = problemSearchParameters.getPageSize().orElse(InitialPagingSizes.INITIAL_PAGE_SIZE);
        int selectedPage = (problemSearchParameters.getPage().orElse(0) < 1) ?
                InitialPagingSizes.INITIAL_PAGE : problemSearchParameters.getPage().get() - 1;

        PageRequest pageRequest = PageRequest.of(selectedPage, selectedPageSize, new Sort(Sort.Direction.ASC, "id"));
        ProblemSearchResult problemSearchResult = new ProblemSearchResult();
        Page<MathProblem> mathProblemPage = mathProblemService.findAllPageable(pageRequest);
        StaticVars.problemsSize = mathProblemPage.getTotalElements();


        if (request.isUserInRole("ROLE_USER")) {
            User user = new User();
            if (session.getAttribute("userType") == "normalUser")
                user = userService.findByEmail(authentication.getName());
            else if (session.getAttribute("userType") == "googleUser")
                user = userService.findByGoogleAuthId(authentication.getName());

            User finalUser = user;
            mathProblemPage.forEach(m -> m.setProblemSolved(finalUser.getMathProblems().contains(m)));
        }

        if (!problemSearchParameters.getPropertyValue().isPresent() || problemSearchParameters.getPropertyValue().get().isEmpty()) {
            problemSearchResult.setMathProblemPage(mathProblemPage);

        } else {
            problemSearchResult = problemFinder.searchProblemByProperty(pageRequest, problemSearchParameters);

            if (problemSearchResult.isNumberFormatException()) {

                return problemSearchErrorResponse.respondToNumberFormatException(problemSearchResult, modelAndView);
            }

            if (problemSearchResult.getMathProblemPage().getTotalElements() == 0) {

                modelAndView = problemSearchErrorResponse.respondToEmptySearchResult(modelAndView, pageRequest, problemSearchParameters);
                problemSearchResult.setMathProblemPage(mathProblemPage);
            }
            modelAndView.addObject("problemsProperty", problemSearchParameters.getProblemsProperty().get());
            modelAndView.addObject("propertyValue", problemSearchParameters.getPropertyValue().get());
        }

        Pager pager = new Pager(problemSearchResult.getMathProblemPage().getTotalPages(), problemSearchResult.getMathProblemPage()
                .getNumber(), InitialPagingSizes.BUTTONS_TO_SHOW, problemSearchResult.getMathProblemPage().getTotalElements());

//        List<ProblemClass> problemClasses = problemClassService.findAll();
//        List<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
//        modelAndView.addObject("classes", problemClasses);

        modelAndView.addObject("problemsPage", problemSearchResult.getMathProblemPage());
        modelAndView.addObject("selectedPageSize", selectedPageSize);
        modelAndView.addObject("page", selectedPage);
        session.setAttribute("page", selectedPage);
        modelAndView.addObject("pageSizes", InitialPagingSizes.PAGE_SIZES);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("problems", true);
        modelAndView.setViewName("problems");
        return modelAndView;
    }


    @GetMapping(value = "/register")
    public String showRegistrationForm(WebRequest request, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if some user is authenticated redirect
        if (!(auth instanceof AnonymousAuthenticationToken)) {

            /* The user is logged in :) */
            return ("redirect:/default");
        } else {
            User user = new User();
            model.addAttribute("user", user);
            return "register";
        }

    }

    @GetMapping(value = "/forgot-password")
    public String forgotPassword(WebRequest request, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if some user is authenticated redirect
        if (!(auth instanceof AnonymousAuthenticationToken)) {

            /* The user is logged in :) */
            return ("redirect:/default");
        } else {

            return "forgot-password";
        }

    }
}
