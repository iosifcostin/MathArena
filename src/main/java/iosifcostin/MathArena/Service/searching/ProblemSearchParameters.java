package iosifcostin.MathArena.Service.searching;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Component
public class ProblemSearchParameters {
    private String problemsProperty;
    private String propertyValue;
    private Integer pageSize;
    private Integer page;

    public Optional<String> getProblemsProperty() {
        return Optional.ofNullable(problemsProperty);
    }

    public Optional<String> getPropertyValue() {
        return Optional.ofNullable(propertyValue);
    }

    public Optional<Integer> getPageSize() {
        return Optional.ofNullable(pageSize);
    }

    public Optional<Integer> getPage() {
        return Optional.ofNullable(page);
    }

    public void setProblemsProperty(String problemsProperty) {
        this.problemsProperty = problemsProperty;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
