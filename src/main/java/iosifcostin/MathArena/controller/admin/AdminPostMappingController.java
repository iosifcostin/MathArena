package iosifcostin.MathArena.controller.admin;

import fmath.ApplicationConfiguration;
import fmath.components.MathMLFormula;
import iosifcostin.MathArena.Service.S3Service.S3Services;
import iosifcostin.MathArena.Service.S3Service.S3ServicesImpl;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.Category;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.ProblemClass;
import iosifcostin.MathArena.Service.CategoryService;
import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.Service.ProblemClassService;
import iosifcostin.MathArena.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminPostMappingController {

    private UserService userService;
    private MathProblemService mathProblemService;
    private CategoryService categoryService;
    private ProblemClassService problemClassService;
    private S3ServicesImpl s3Services;

    public AdminPostMappingController(UserService userService, MathProblemService mathProblemService, CategoryService categoryService, ProblemClassService problemClassService, S3ServicesImpl s3Services) {
        this.userService = userService;
        this.mathProblemService = mathProblemService;
        this.categoryService = categoryService;
        this.problemClassService = problemClassService;
        this.s3Services = s3Services;
    }

    @PostMapping(value = "/saveProblem", params = "action=save")
    public ModelAndView saveProblem(ModelAndView modelAndView, Model model,
                                    @ModelAttribute("mathProblem") @Valid final MathProblem mathProblem,
                                    BindingResult bindingResult, HttpSession session,
                                    HttpServletRequest request, Errors errors) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("classes", problemClassService.findAll());

        MathProblem nameExist = mathProblemService.findByName(mathProblem.getName());

        if (nameExist != null) {
            model.addAttribute("description", mathProblem.getDescription());
            modelAndView.setViewName("admin/addProblem");
            bindingResult.rejectValue("name", "problemNameExist",
                    "There is already a problem registered with that name");
        }


        if (bindingResult.hasErrors()) {
            model.addAttribute("description", mathProblem.getDescription());
            modelAndView.setViewName("admin/addProblem");
        } else {
            convertMathAndStoreOnAwsS3(mathProblem,null, true);

            session.setAttribute("problemSaved", "Problem Saved");

            modelAndView.setViewName("redirect:/admin/addProblem");
        }


        return modelAndView;
    }

    @PostMapping(value = "/saveProblem", params = "action=edit")
    public ModelAndView editProblem(ModelAndView modelAndView, Model model,
                                    @ModelAttribute("mathProblem") @Valid final MathProblem mathProblem,
                                    BindingResult bindingResult, HttpSession session,
                                    HttpServletRequest request, Errors errors) {

        MathProblem nameExist = mathProblemService.findByName(mathProblem.getName());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("classes", problemClassService.findAll());

        if (nameExist != null && !nameExist.getName().equals(session.getAttribute("initialName"))) {
            model.addAttribute("description", mathProblem.getDescription());
            modelAndView.setViewName("admin/addProblem");
            bindingResult.rejectValue("name", "problemNameExist",
                    "There is already a problem registered with that name");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("description", mathProblem.getDescription());
            model.addAttribute("editForm", true);
            modelAndView.setViewName("admin/addProblem");
        } else {

            convertMathAndStoreOnAwsS3(mathProblem, (Long) session.getAttribute("InitialId"), false);
            session.setAttribute("problemEdited", "Problem Edited");

            modelAndView.setViewName("redirect:/admin/problems");
        }

        return modelAndView;
    }

    @PostMapping(value = "/saveCategory", params = "action=save")
    public ModelAndView saveCategory(ModelAndView modelAndView,
                                     @ModelAttribute("category") @Valid final Category category,
                                     BindingResult bindingResult, HttpSession session,
                                     HttpServletRequest request, Errors errors) {

        Category catExist = categoryService.findByName(category.getCategoryName());

        if (catExist != null) {
            modelAndView.setViewName("admin/addCategory");
            bindingResult.rejectValue("categoryName", "categoryExist",
                    "There is already a category registered with that name");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/addCategory");
        } else {

            categoryService.save(category);

            session.setAttribute("categorySaved", "Category Saved");

            modelAndView.setViewName("redirect:/admin/addCategory");
        }

        return modelAndView;
    }

    @PostMapping(value = "/saveCategory", params = "action=edit")
    public ModelAndView editCategory(ModelAndView modelAndView,
                                     @ModelAttribute("category") @Valid final Category category,
                                     BindingResult bindingResult, HttpSession session,
                                     HttpServletRequest request, Errors errors) {

        Category catExist = categoryService.findByName(category.getCategoryName());

        if (catExist != null && !catExist.getCategoryName().equals(session.getAttribute("initialName"))) {
            modelAndView.setViewName("admin/addCategory");
            bindingResult.rejectValue("categoryName", "categoryExist",
                    "There is already a category registered with that name");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/addCategory");
        } else {

            categoryService.edit(category, (Long) session.getAttribute("initialId"));

            session.setAttribute("categorySaved", "Category Saved");

            modelAndView.setViewName("redirect:/admin/addCategory");
        }

        return modelAndView;
    }


    @PostMapping(value = "/saveClass", params = "action=save")
    public ModelAndView saveClass(ModelAndView modelAndView,
                                  @ModelAttribute("problemClass") @Valid final ProblemClass problemClass,
                                  BindingResult bindingResult, HttpSession session,
                                  HttpServletRequest request, Errors errors) {

        ProblemClass nameExist = problemClassService.findByName(problemClass.getProblemClassName());

        if (nameExist != null && !nameExist.getProblemClassName().equals(session.getAttribute("initialName"))) {
            modelAndView.setViewName("admin/addClass");
            bindingResult.rejectValue("problemClassName", "classExist",
                    "There is already a class registered with that name");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/addClass");
        } else {

            problemClassService.save(problemClass);

            session.setAttribute("classSaved", "Class Saved");

            modelAndView.setViewName("redirect:/admin/addClass");
        }

        return modelAndView;
    }

    @PostMapping(value = "/saveClass", params = "action=edit")
    public ModelAndView editClass(ModelAndView modelAndView,
                                  @ModelAttribute("problemClass") @Valid final ProblemClass problemClass,
                                  BindingResult bindingResult, HttpSession session,
                                  HttpServletRequest request, Errors errors) {

        ProblemClass nameExist = problemClassService.findByName(problemClass.getProblemClassName());

        if (nameExist != null) {
            modelAndView.setViewName("admin/addClass");
            bindingResult.rejectValue("problemClassName", "classExist",
                    "There is already a class registered with that name");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/addClass");
        } else {

            problemClassService.edit(problemClass, (Long) session.getAttribute("initialId"));

            session.setAttribute("classSaved", "Class Saved");

            modelAndView.setViewName("redirect:/admin/addClass");
        }

        return modelAndView;
    }

    private void convertMathAndStoreOnAwsS3(MathProblem mathProblem,Long id, boolean save) {
        String descriptionFileName = "problem" + System.currentTimeMillis() + ".png";
        String resultFileName = "result" + System.currentTimeMillis() + ".png";

        String folderFonts = "fonts";
        String folderGlyphs = "glyphs";

        ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
        ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyphs);
        ApplicationConfiguration.setWebApp(false);

        MathMLFormula formula = new MathMLFormula();
        BufferedImage imgDescription = formula.drawImage(mathProblem.getDescription());
        BufferedImage imgResult = formula.drawImage(mathProblem.getResult());

        s3Services.uploadBufferedImageToServer(imgDescription, descriptionFileName, "png", mathProblem.getDescriptionPath());
        s3Services.uploadBufferedImageToServer(imgResult, resultFileName, "png", mathProblem.getResultPath());

        mathProblem.setDatePosted(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
        mathProblem.setDescriptionPath("https://matharena.s3.eu-central-1.amazonaws.com/" + descriptionFileName);
        mathProblem.setResultPath("https://matharena.s3.eu-central-1.amazonaws.com/" + resultFileName);

        if (save)
        mathProblemService.save(mathProblem);
        else
        mathProblemService.edit(mathProblem, id);

    }
}
