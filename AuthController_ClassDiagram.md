# AuthController 클래스 다이어그램

## Mermaid 형식

```mermaid
classDiagram
    class AuthController {
        -UserService userService
        +signup(String, String, String) String
        +login(String, String, HttpSession) String
        +getLoginUser(HttpSession) Object
        +logout(HttpSession) String
    }
    
    class UserService {
        -UserRepository userRepo
        -BCryptPasswordEncoder passwordEncoder
        +register(String, String, String) void
        +login(String, String) User
    }
    
    class User {
        -Long id
        -String username
        -String email
        -String password
        +getId() Long
        +getUsername() String
        +getEmail() String
        +getPassword() String
        +setId(Long) void
        +setUsername(String) void
        +setEmail(String) void
        +setPassword(String) void
    }
    
    class UserRepository {
        <<interface>>
        +existsByUsername(String) boolean
        +existsByEmail(String) boolean
        +findByUsername(String) User
        +findByEmail(String) User
    }
    
    class BCryptPasswordEncoder {
        +encode(CharSequence) String
        +matches(CharSequence, String) boolean
    }
    
    class HttpSession {
        +setAttribute(String, Object) void
        +getAttribute(String) Object
        +invalidate() void
    }
    
    class HashMap {
        +put(K, V) V
    }
    
    class Map {
        <<interface>>
    }
    
    AuthController --> UserService : uses
    AuthController --> User : returns
    AuthController --> HttpSession : uses
    AuthController --> HashMap : creates
    AuthController ..> Map : uses
    
    UserService --> UserRepository : uses
    UserService --> User : creates/returns
    UserService --> BCryptPasswordEncoder : uses
    
    UserRepository ..|> JpaRepository : extends
    UserRepository --> User : manages
    
    HashMap ..|> Map : implements
```

## 클래스 설명

### AuthController
- **역할**: 인증 관련 HTTP 요청을 처리하는 Spring Controller
- **주요 메서드**:
  - `signup()`: 회원가입 처리
  - `login()`: 로그인 처리 및 세션 저장
  - `getLoginUser()`: 현재 로그인된 사용자 정보 조회
  - `logout()`: 로그아웃 처리 (세션 무효화)

### UserService
- **역할**: 사용자 관련 비즈니스 로직 처리
- **주요 메서드**:
  - `register()`: 사용자 등록 (이메일/비밀번호 검증 포함)
  - `login()`: 사용자 로그인 검증

### User
- **역할**: 사용자 엔티티 (JPA)
- **속성**: id, username, email, password

### UserRepository
- **역할**: 사용자 데이터 접근을 위한 JPA Repository 인터페이스

