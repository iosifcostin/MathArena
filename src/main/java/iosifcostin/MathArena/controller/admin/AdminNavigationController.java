package iosifcostin.MathArena.controller.admin;

import iosifcostin.MathArena.Service.*;
import iosifcostin.MathArena.Service.searching.UserFinder;
import iosifcostin.MathArena.Service.searching.UserSearchErrorResponse;
import iosifcostin.MathArena.Service.searching.UserSearchParameters;
import iosifcostin.MathArena.Service.searching.UserSearchResult;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.*;
import iosifcostin.MathArena.paging.InitialPagingSizes;
import iosifcostin.MathArena.paging.Pager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminNavigationController {

    private MathProblemService mathProblemService;
    private CategoryService categoryService;
    private ProblemClassService problemClassService;
    private MathMlToPng mathMlToPng;
    private UserFinder userFinder;
    private UserService userService;
    private UserSearchErrorResponse userSearchErrorResponse;
    private RoleService roleService;

    public AdminNavigationController(MathProblemService mathProblemService, CategoryService categoryService, ProblemClassService problemClassService, MathMlToPng mathMlToPng, UserFinder userFinder, UserService userService, UserSearchErrorResponse userSearchErrorResponse, RoleService roleService) {
        this.mathProblemService = mathProblemService;
        this.categoryService = categoryService;
        this.problemClassService = problemClassService;
        this.mathMlToPng = mathMlToPng;
        this.userFinder = userFinder;
        this.userService = userService;
        this.userSearchErrorResponse = userSearchErrorResponse;
        this.roleService = roleService;
    }

    @GetMapping("/index")
    public String adminIndex(Model model) {
        model.addAttribute("index", true);

        return "admin/index";
    }

    @GetMapping("/problems")
    public String problems(@RequestParam("pageSize") Optional<Integer> pageSize,
                           @RequestParam("page") Optional<Integer> page, Model model, HttpSession session) {

        if (session.getAttribute("problemEdited") != null) {
            model.addAttribute("problemEdited", session.getAttribute("problemEdited"));
            session.removeAttribute("problemEdited");
        }
        if (session.getAttribute("problemRemoved") != null) {
            model.addAttribute("problemRemoved", session.getAttribute("problemRemoved"));
            session.removeAttribute("problemRemoved");
        }
        int evalPage = (page.orElse(0) < 1) ? InitialPagingSizes.INITIAL_PAGE : page.get() - 1;
        int evalPageSize = pageSize.orElse(InitialPagingSizes.BUTTONS_TO_SHOW);


        Page<MathProblem> mathProblems = mathProblemService.findAllPageable(PageRequest.of(evalPage, evalPageSize));

        mathProblems.forEach(mathProblem -> {
            mathProblem.setResultDto(mathMlToPng.convertMathMl(mathProblem.getResult()));
            mathProblem.setDescriptionDto(mathMlToPng.convertMathMl(mathProblem.getDescription()));
        });

        Pager pager = new Pager(mathProblems.getTotalPages(), mathProblems.getNumber(), InitialPagingSizes.BUTTONS_TO_SHOW, mathProblems.getTotalElements());
        model.addAttribute("problemsPage", mathProblems);
        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("page", evalPage);
        model.addAttribute("pageSizes", InitialPagingSizes.PAGE_SIZES);
        model.addAttribute("pager", pager);
        model.addAttribute("problems", true);

        return "admin/problems";
    }


    @GetMapping("/users")
    public ModelAndView getUsers (ModelAndView modelAndView, UserSearchParameters userSearchParameters) {

        int selectedPageSize = userSearchParameters.getPageSize().orElse(InitialPagingSizes.INITIAL_PAGE_SIZE);
        int selectedPage = (userSearchParameters.getPage().orElse(0) < 1) ?
                InitialPagingSizes.INITIAL_PAGE : userSearchParameters.getPage().get() - 1;

        PageRequest pageRequest = PageRequest.of(selectedPage, selectedPageSize, new Sort(Sort.Direction.ASC, "id"));
        UserSearchResult userSearchResult = new UserSearchResult();

        //Empty search parameters
        if (!userSearchParameters.getPropertyValue().isPresent() || userSearchParameters.getPropertyValue().get().isEmpty())
            userSearchResult.setUserPage(userService.findAllPageable(pageRequest));

            //Search queries
        else {
            userSearchResult = userFinder.searchUsersByProperty(pageRequest, userSearchParameters);

            if (userSearchResult.isNumberFormatException())
                return userSearchErrorResponse.respondToNumberFormatException(userSearchResult, modelAndView);

            if (userSearchResult.getUserPage().getTotalElements() == 0){
                modelAndView = userSearchErrorResponse.respondToEmptySearchResult(modelAndView, pageRequest);
                userSearchResult.setUserPage(userService.findAllPageable(pageRequest));
            }
            modelAndView.addObject("usersProperty", userSearchParameters.getUsersProperty().get());
            modelAndView.addObject("propertyValue", userSearchParameters.getPropertyValue().get());
        }

        Pager pager = new Pager(userSearchResult.getUserPage().getTotalPages(), userSearchResult.getUserPage()
                .getNumber(), InitialPagingSizes.BUTTONS_TO_SHOW, userSearchResult.getUserPage().getTotalElements());
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("users", userSearchResult.getUserPage());
        modelAndView.addObject("selectedPageSize", selectedPageSize);
        modelAndView.addObject("pageSizes", InitialPagingSizes.PAGE_SIZES);
        modelAndView.addObject("usersPage", true);
        modelAndView.setViewName("admin/users");
        return modelAndView;
    }

    @GetMapping("/users/{id}")
    public String getEditUserForm(@PathVariable Long id, Model model) {

        User user = userService.findByIdEagerly(id);

        List<Role> allRoles = roleService.findAll();

        user.setRoles(userService.getAssignedRolesList(user));

        model.addAttribute("userUpdateDto", user);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("usersPage", true);
        return "admin/editUser";
    }

    @PostMapping("/users/{id}")
    public String updateUser(Model model, @PathVariable Long id, @ModelAttribute("oldUser") @Valid final User userUpdateDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        String formWithErrors = "admin/editUser";

        Optional<User> persistedUser = userService.findById(id);
        List<Role> allRoles = roleService.findAll();

        boolean hasErrors = false;

        if (bindingResult.hasErrors()) hasErrors = true;

        if (hasErrors) {
            model.addAttribute("userUpdateDto", userUpdateDto);
            model.addAttribute("rolesList", allRoles);
            model.addAttribute("org.springframework.validation.BindingResult.userUpdateDto", bindingResult);
            return formWithErrors;
        }
        else {
            persistedUser.get().setRoles(userService.getAssignedRolesList(userUpdateDto));

            userService.save(persistedUser.get());
            redirectAttributes.addFlashAttribute("userHasBeenUpdated", true);
            return "redirect:/admin/users";
        }
    }


    @GetMapping("/addProblem")
    public String addProblem(Model model, HttpSession session) {
        if (session.getAttribute("problemSaved") != null) {
            model.addAttribute("problemSaved", session.getAttribute("problemSaved"));
            session.removeAttribute("problemSaved");
        }
        if (session.getAttribute("categorySaved") != null) {
            model.addAttribute("categorySaved", session.getAttribute("categorySaved"));
            session.removeAttribute("categorySaved");
        }

        MathProblem mathProblem = new MathProblem();
        List<Category> categories = categoryService.findAll();
        List<ProblemClass> problemClasses = problemClassService.findAll();
        model.addAttribute("classes", problemClasses);
        model.addAttribute("mathProblem", mathProblem);
        model.addAttribute("categories", categories);
        model.addAttribute("addProblem", true);

        return "admin/addProblem";
    }

    @GetMapping("/addCategory")
    public String addCategory(Model model, HttpSession session) {

        if (session.getAttribute("categorySaved") != null) {
            model.addAttribute("categorySaved", session.getAttribute("categorySaved"));
            session.removeAttribute("categorySaved");
        }

        Category category = new Category();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        model.addAttribute("addProblem", true);

        return "admin/addCategory";
    }

    @GetMapping("/addClass")
    public String addClass(Model model, HttpSession session) {

        if (session.getAttribute("classSaved") != null) {
            model.addAttribute("classSaved", session.getAttribute("classSaved"));
            session.removeAttribute("classSaved");
        }

        ProblemClass problemClass = new ProblemClass();
        List<ProblemClass> problemClasses = problemClassService.findAll();
        model.addAttribute("classes", problemClasses);
        model.addAttribute("problemClass", problemClass);
        model.addAttribute("addClass", true);

        return "admin/addClass";
    }
}
