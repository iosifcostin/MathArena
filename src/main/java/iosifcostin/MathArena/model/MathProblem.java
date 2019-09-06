package iosifcostin.MathArena.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import iosifcostin.MathArena.customAnnotations.MathMlNotEmpty;
import iosifcostin.MathArena.customAnnotations.ValidMathMlFormat;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class MathProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true, columnDefinition = "TEXT")
    @MathMlNotEmpty
    private String description;

    private String descriptionPath;

    @Column(nullable = false)
    @ValidMathMlFormat
    private String result;

    private String resultPath;

    @Transient
    private boolean problemSolved;


    private String datePosted;

    @ManyToOne()
    private Category category;

    @ManyToOne()
    private ProblemClass problemClass;

    @JsonManagedReference
    @ManyToMany(mappedBy = "mathProblems", cascade = CascadeType.MERGE)
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public boolean isProblemSolved() {
        return problemSolved;
    }

    public void setProblemSolved(boolean problemSolved) {
        this.problemSolved = problemSolved;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ProblemClass getProblemClass() {
        return problemClass;
    }

    public void setProblemClass(ProblemClass problemClass) {
        this.problemClass = problemClass;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
