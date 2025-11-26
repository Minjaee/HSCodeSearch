package HSCodeSearch.HSCodeSearch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // templates/login.html 렌더링
    }


    @GetMapping("/signup")
    public String signUpPage() {
        return "signup";  // signup.html
    }

}



