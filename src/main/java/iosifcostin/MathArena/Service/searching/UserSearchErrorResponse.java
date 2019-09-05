package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.paging.InitialPagingSizes;
import iosifcostin.MathArena.paging.Pager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class UserSearchErrorResponse {

    private UserService userService;

    public UserSearchErrorResponse(UserService userService) {
        this.userService = userService;
    }

    public ModelAndView respondToNumberFormatException(UserSearchResult userSearchResult, ModelAndView modelAndView) {
        Pager pager = new Pager(userSearchResult.getUserPage().getTotalPages(),
                userSearchResult.getUserPage().getNumber(), InitialPagingSizes.BUTTONS_TO_SHOW,
                userSearchResult.getUserPage().getTotalElements());

        modelAndView.addObject("numberFormatException", true);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("users", userSearchResult.getUserPage());
        modelAndView.setViewName("admin/users");
        return modelAndView;
    }

    public ModelAndView respondToEmptySearchResult(ModelAndView modelAndView, PageRequest pageRequest) {
        modelAndView.addObject("noMatches", true);
        modelAndView.addObject("users", userService.findAllPageable(pageRequest));
        return modelAndView;
    }
}
