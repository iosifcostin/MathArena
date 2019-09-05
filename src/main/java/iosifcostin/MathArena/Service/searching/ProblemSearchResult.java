package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.MathProblem;
import org.springframework.data.domain.Page;


//@AllArgsConstructor
//@NoArgsConstructor
public class ProblemSearchResult {
    private Page<MathProblem> mathProblemPage;
    private boolean numberFormatException;

    public ProblemSearchResult(Page<MathProblem> mathProblemPage, boolean numberFormatException) {
        this.mathProblemPage = mathProblemPage;
        this.numberFormatException = numberFormatException;
    }

    public ProblemSearchResult() {

    }


    public Page<MathProblem> getMathProblemPage() {

        return mathProblemPage;
    }

    public void setMathProblemPage(Page<MathProblem> mathProblemPage) {

        this.mathProblemPage = mathProblemPage;
    }

    public boolean isNumberFormatException() {
        return numberFormatException;
    }

    public void setNumberFormatException(boolean numberFormatException) {
        this.numberFormatException = numberFormatException;
    }
}
