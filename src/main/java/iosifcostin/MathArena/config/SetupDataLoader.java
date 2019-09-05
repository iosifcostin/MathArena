package iosifcostin.MathArena.config;

import iosifcostin.MathArena.Service.*;
import iosifcostin.MathArena.model.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private UserService userService;
    private MathProblemService mathProblemService;
    private RoleService roleService;
    private CategoryService categoryService;
    private ProblemClassService problemClassService;


    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SetupDataLoader( UserService userService, MathProblemService mathProblemService, RoleService roleService, CategoryService categoryService, ProblemClassService problemClassService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.mathProblemService = mathProblemService;
        this.roleService = roleService;
        this.categoryService = categoryService;
        this.problemClassService = problemClassService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }


        //region Creating roles
        //================================================================================
        Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN");
        Role roleUser = createRoleIfNotFound("ROLE_USER");


        byte[] profilePicture = convertPicture("src/main/resources/static/images/no-image.jpg");


        List<Role> userRoles = Collections.singletonList(roleUser);
        List<Role> rootRoles = Collections.singletonList(roleAdmin);


        //================================================================================
        //endregion


        //region Creating users
        //================================================================================
        createUserIfNotFound("admin@gmail.com", "MathArena", "Admin", "admin", rootRoles, profilePicture);

        for (int i = 1; i < 4; i++) {
            createUserIfNotFound("user" + i + "@gmail.com", "fName" + i, "lName" + i,
                    "user" + i, userRoles, profilePicture);
        }
        //================================================================================
        //endregion


        alreadySetup = true;
    }

    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleService.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleService.save(role);
        }
        return role;
    }


    @Transactional
    void createUserIfNotFound(final String email, String firstName, String lastName,
                              String password, List<Role> userRoles, byte[] profilePicture) {
        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setEmail(email);
            user.setRoles(userRoles);
            user.setEnabled(true);
            user.setProfilePicture(profilePicture);
            userService.save(user);
        }
    }

    private byte[] convertPicture(String filePath) {
        BufferedImage squareImage;
        try {

            squareImage = ImageIO.read(new File(filePath));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(squareImage, "jpg", baos);
//            return Base64.getEncoder().encodeToString(baos.toByteArray());
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}