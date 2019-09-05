package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Data
@Service
public class UserFinder {
    private UserService userService;

    @Autowired
    public UserFinder(UserService userService) {
        this.userService = userService;
    }

    public UserSearchResult searchUsersByProperty(PageRequest pageRequest, UserSearchParameters userSearchParameters) {
        Page<User> userPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        switch (userSearchParameters.getUsersProperty().get()) {
            case "ID":
                try {
                    long id = Long.parseLong(userSearchParameters.getPropertyValue().get());
                    userPage = userService.findByIdPageable(id, pageRequest);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return new UserSearchResult(userService.findAllPageable(pageRequest), true);
                }
                break;
            case "FirstName":
                userPage = userService.findByFirstNameContaining(userSearchParameters.getPropertyValue().get(), pageRequest);
                break;
            case "LastName":
                userPage = userService.findByLastNameContaining(userSearchParameters.getPropertyValue().get(), pageRequest);
                break;
            case "Email":
                userPage = userService.findByEmailContaining(userSearchParameters.getPropertyValue().get(), pageRequest);
                break;
        }
        return new UserSearchResult(userPage, false);
    }
}
