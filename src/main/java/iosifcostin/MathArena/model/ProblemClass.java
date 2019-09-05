package iosifcostin.MathArena.model;


import javax.persistence.*;
import java.util.List;

@Entity
public class ProblemClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String problemClassName;

    @OneToMany(mappedBy = "problemClass",orphanRemoval = true, cascade= CascadeType.ALL)
    private List<MathProblem> mathProblems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProblemClassName() {
        return problemClassName;
    }

    public void setProblemClassName(String problemClassName) {
        this.problemClassName = problemClassName;
    }

    public List<MathProblem> getMathProblems() {
        return mathProblems;
    }

    public void setMathProblems(List<MathProblem> mathProblems) {
        this.mathProblems = mathProblems;
    }
}
