package iosifcostin.MathArena.model;


import javax.persistence.*;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category",orphanRemoval = true, cascade= CascadeType.ALL)
    private List<MathProblem> mathProblems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<MathProblem> getMathProblems() {
        return mathProblems;
    }

    public void setMathProblems(List<MathProblem> mathProblems) {
        this.mathProblems = mathProblems;
    }
}
