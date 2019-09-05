package iosifcostin.MathArena.controller.admin;


import iosifcostin.MathArena.model.Category;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.ProblemClass;
import iosifcostin.MathArena.Service.CategoryService;
import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.Service.ProblemClassService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class EditRemoveController {

    private MathProblemService mathProblemService;
    private CategoryService categoryService;
    private ProblemClassService problemClassService;

    public EditRemoveController(MathProblemService mathProblemService, CategoryService categoryService, ProblemClassService problemClassService) {
        this.mathProblemService = mathProblemService;
        this.categoryService = categoryService;
        this.problemClassService = problemClassService;
    }

    @GetMapping("/editProblem/{id}")
    public String editProblem(@PathVariable Long id,Model model, HttpSession session) {

        MathProblem mathProblem = mathProblemService.findById(id);

        model.addAttribute("description", mathProblem.getDescription());
//        model.addAttribute("result", mathProblem.getResult());
        model.addAttribute("editForm", true);
        model.addAttribute("editId", mathProblem.getId());
        model.addAttribute("mathProblem", mathProblem);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("classes", problemClassService.findAll());
        session.setAttribute("initialName", mathProblem.getName());
        session.setAttribute("initialId",mathProblem.getId());

        return "admin/addProblem";
    }

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable Long id,Model model, HttpSession session) {

        Category category = categoryService.findById(id);

        model.addAttribute("editForm", true);
        model.addAttribute("editId", category.getId());
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.findAll());
        session.setAttribute("initialName", category.getCategoryName());
        session.setAttribute("initialId",category.getId());

        return "admin/addCategory";
    }

    @GetMapping("/editProblemClass/{id}")
    public String editProblemClass(@PathVariable Long id,Model model, HttpSession session) {

        ProblemClass problemClass = problemClassService.findById(id);
        model.addAttribute("editForm", true);
        model.addAttribute("editId", problemClass.getId());
        model.addAttribute("problemClass", problemClass);
        model.addAttribute("classes", problemClassService.findAll());
        session.setAttribute("initialName", problemClass.getProblemClassName());
        session.setAttribute("initialId",problemClass.getId());

        return "admin/addClass";
    }

    @GetMapping("/removeProblem/{id}")
    public void removeFromMainTable(@PathVariable Long id, HttpServletResponse response, HttpSession session) {

        mathProblemService.deleteById(id);

        session.setAttribute("problemRemoved", "Problem removed");

        try {
            response.sendRedirect("/admin/problems");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
