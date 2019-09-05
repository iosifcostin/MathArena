package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.MathProblem;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Data
@Service
public class ProblemFinder {
    private MathProblemService mathProblemService;
    private MathMlToPng mathMlToPng;

    @Autowired
    public ProblemFinder(MathProblemService mathProblemService, MathMlToPng mathMlToPng) {
        this.mathProblemService = mathProblemService;
        this.mathMlToPng = mathMlToPng;
    }

    public ProblemSearchResult searchProblemByProperty(PageRequest pageRequest, ProblemSearchParameters problemSearchParameters) {
        Page<MathProblem> mathProblemPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        switch (problemSearchParameters.getProblemsProperty().get()) {
            case "ID":
                try {
                    long id = Long.parseLong(problemSearchParameters.getPropertyValue().get());
                    mathProblemPage = mathProblemService.findByIdPageable(id, pageRequest);
                    mathProblemPage.forEach(mathProblem -> {
                        mathProblem.setDescriptionDto(mathMlToPng.convertMathMl(mathProblem.getDescription()));
                        mathProblem.setResultDto(mathMlToPng.convertMathMl(mathProblem.getResult()));
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return new ProblemSearchResult(mathProblemService.findAllPageable(pageRequest), true);
                }
                break;
            case "Nume":
                mathProblemPage = mathProblemService.findByNameContaining(problemSearchParameters.getPropertyValue().get(), pageRequest);
                mathProblemPage.forEach(mathProblem -> {
                    mathProblem.setDescriptionDto(mathMlToPng.convertMathMl(mathProblem.getDescription()));
                    mathProblem.setResultDto(mathMlToPng.convertMathMl(mathProblem.getResult()));
                });
                break;
        }
        return new ProblemSearchResult(mathProblemPage, false);
    }
}
