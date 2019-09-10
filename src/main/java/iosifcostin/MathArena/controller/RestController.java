package iosifcostin.MathArena.controller;

import iosifcostin.MathArena.Service.CategoryService;
import iosifcostin.MathArena.Service.MathProblemService;
import iosifcostin.MathArena.Service.ProblemClassService;
import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private MathProblemService mathProblemService;

    private UserService userService;

    public RestController(MathProblemService mathProblemService, UserService userService) {
        this.mathProblemService = mathProblemService;
        this.userService = userService;
    }

    @GetMapping("/getProblem")
    public ResponseEntity<MathProblem> getProblem(@RequestParam Long id) {

        MathProblem mathProblem = mathProblemService.findById(id);

        return new ResponseEntity<>(mathProblem, HttpStatus.OK);

    }

    @GetMapping("/checkAnswer")
    public ResponseEntity<Boolean> checkAnswer(@RequestParam String answer, @RequestParam Long id, Authentication authentication, HttpSession session) {

        MathProblem mathProblem = mathProblemService.findById(id);
        Boolean isValid = mathProblem.getResult().equals(answer);

        if (isValid) {
            User user;
            if (userService.isOauth(authentication))
                user = userService.findByClientAuthId(authentication.getName());
            else
                user = userService.findByEmail(authentication.getName());

            List<MathProblem> problems = userService.getAssignedProblemsList(user);
            problems.add(mathProblem);
            user.setMathProblems(problems);
            userService.save(user);
        }

        return new ResponseEntity<>(isValid, HttpStatus.OK);

    }

    @DeleteMapping("/admin/json-users/delete/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        Optional<User> userToDelete = userService.findById(id);

        if (!userToDelete.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        else {
            userService.deleteById(id);
            return new ResponseEntity<>(userToDelete.get(), HttpStatus.NO_CONTENT);
        }
    }

//    @GetMapping("/getProblems")
//    public ResponseEntity<List<MathProblem>> getMessages(@RequestParam("pageSize") Optional<Integer> pageSize,
//                                                         @RequestParam("page") Optional<Integer> page) {
//
//        int evalPage = (page.orElse(0) < 1) ? InitialPagingSizes.INITIAL_PAGE : page.get() - 1;
//        int evalPageSize = pageSize.orElse(InitialPagingSizes.BUTTONS_TO_SHOW);
//
//        Page<MathProblem> mathProblems = mathProblemService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
//        mathProblems.forEach(mathProblem -> {
//            mathProblem.setResultDto(mathMlToPng.convertMathMl(mathProblem.getResult()));
//            mathProblem.setDescriptionDto(mathMlToPng.convertMathMl(mathProblem.getDescription()));
//        });
//
//        if (mathProblems.getContent() == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//        else if (mathProblems.isEmpty()) return new ResponseEntity<>(mathProblems.getContent(), HttpStatus.NOT_FOUND);
//
//        else return new ResponseEntity<>(mathProblems.getContent(), HttpStatus.OK);
//    }
}
