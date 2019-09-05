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

    @Transient
    private String descriptionDto;
    @Transient
    private String resultDto;
    @Transient
    private boolean problemSolved;

    @Column(nullable = false)
    @ValidMathMlFormat
    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
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

    public String getDescriptionDto() {
        return descriptionDto;
    }

    public void setDescriptionDto(String descriptionDto) {
        this.descriptionDto = descriptionDto;
    }

    public String getResultDto() {
        return resultDto;
    }

    public void setResultDto(String resultDto) {
        this.resultDto = resultDto;
    }

    public Boolean getProblemSolved() {
        return problemSolved;
    }

    public void setProblemSolved(Boolean problemSolved) {
        this.problemSolved = problemSolved;
    }
}
