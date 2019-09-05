package iosifcostin.MathArena.Service.searching;

import iosifcostin.MathArena.model.User;
import org.springframework.data.domain.Page;


//@AllArgsConstructor
//@NoArgsConstructor
public class UserSearchResult {
    private Page<User> userPage;
    private boolean numberFormatException;

    UserSearchResult(Page<User> userPage, boolean numberFormatException) {
        this.userPage = userPage;
        this.numberFormatException = numberFormatException;
    }

    public UserSearchResult() {

    }


    public Page<User> getUserPage() {
        return userPage;
    }

    public void setUserPage(Page<User> userPage) {
        this.userPage = userPage;
    }

    public boolean isNumberFormatException() {
        return numberFormatException;
    }

    public void setNumberFormatException(boolean numberFormatException) {
        this.numberFormatException = numberFormatException;
    }
}
