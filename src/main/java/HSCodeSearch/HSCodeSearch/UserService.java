package HSCodeSearch.HSCodeSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(String username, String email, String password) {

        // 이메일 형식 검사
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        // 비밀번호 규칙 검사
        String pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        if (!password.matches(pwRegex)) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 영어/숫자/특수문자(@$!%*#?&)를 모두 포함해야 합니다.");
        }

        // 중복 체크
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용중인 Username입니다.");
        }

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepo.save(user);
    }

    public User login(String usernameOrEmail, String password) {

        // 이메일인지 아이디인지 자동 판단
        User user;

        if (usernameOrEmail.contains("@")) {
            user = userRepo.findByEmail(usernameOrEmail);
        } else {
            user = userRepo.findByUsername(usernameOrEmail);
        }

        if (user == null) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

}
