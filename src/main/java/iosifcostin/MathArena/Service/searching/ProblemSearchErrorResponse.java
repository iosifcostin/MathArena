package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.paging.InitialPagingSizes;
import iosifcostin.MathArena.paging.Pager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class ProblemSearchErrorResponse {

    private MathProblemService mathProblemService;

    public ProblemSearchErrorResponse(MathProblemService mathProblemService) {
        this.mathProblemService = mathProblemService;
    }

    public ModelAndView respondToNumberFormatException(ProblemSearchResult problemSearchResult, ModelAndView modelAndView) {
        Pager pager = new Pager(problemSearchResult.getMathProblemPage().getTotalPages(),
                problemSearchResult.getMathProblemPage().getNumber(), InitialPagingSizes.BUTTONS_TO_SHOW,
                problemSearchResult.getMathProblemPage().getTotalElements());


        modelAndView.addObject("numberFormatException", "Introdu un numar valid");
        modelAndView.addObject("problems", true);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("pageSizes", InitialPagingSizes.PAGE_SIZES);
        modelAndView.addObject("selectedPageSize", InitialPagingSizes.INITIAL_PAGE_SIZE);
        modelAndView.addObject("problemsPage", problemSearchResult.getMathProblemPage());
        modelAndView.setViewName("problems");
        return modelAndView;
    }

    public ModelAndView respondToEmptySearchResult(ModelAndView modelAndView, PageRequest pageRequest, ProblemSearchParameters problemSearchParameters) {

        modelAndView.addObject("noMatches", "Nu am gasit rezultat pentru " +
                problemSearchParameters.getProblemsProperty().get() + " = " + problemSearchParameters.getPropertyValue().get());
        problemSearchParameters.setProblemsProperty("");
        problemSearchParameters.setPropertyValue("");

        modelAndView.addObject("problemsPage", mathProblemService.findAllPageable(pageRequest));
        return modelAndView;
    }
}
