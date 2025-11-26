package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // 로그인용
    User findByUsername(String username);
    User findByEmail(String email);
}
