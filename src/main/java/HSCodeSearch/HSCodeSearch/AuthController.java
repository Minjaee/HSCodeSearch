package HSCodeSearch.HSCodeSearch;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;



@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseBody
    public String signup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password
    ) {
        try {
            userService.register(username, email, password);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/api/login")
    @ResponseBody
    public String login(
            @RequestParam String id,        // username or email
            @RequestParam String password,
            HttpSession session
    ) {
        try {
            User user = userService.login(id, password);

            // 로그인 세션 저장
            session.setAttribute("loginUser", user);

            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/api/user")
    @ResponseBody
    public Object getLoginUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return null;
        }

        // email / username만 보내기
        Map<String, Object> info = new HashMap<>();
        info.put("username", loginUser.getUsername());
        info.put("email", loginUser.getEmail());

        return info;
    }

    @PostMapping("/logout")
    @ResponseBody
    public String logout(HttpSession session) {
        session.invalidate();
        return "success";
    }


}
