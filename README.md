# Project Architecture Overview

본 프로젝트는 **Spring Boot 기반 웹 애플리케이션**으로,  
**MVC 패턴을 기반으로 한 계층형 아키텍처** 를 사용합니다.


---

## Overall Structure

```
Controller → Service → Repository → Entity (DB)
        ↓
       DTO
        ↓
      View (HTML / JSON)
```

# Entity Layer (Domain / Model)
```
User
// 사용자 정보 (회원 계정, 인증 정보)

HsCode
// HS Code 기본 정보 (코드, 설명)

Bookmark
// 사용자 북마크 정보
// User ↔ HsCode 관계 엔티티

History
// 사용자 검색 기록
// 검색 이력 관리

UsTariff
// 미국 관세 정보
// HS Code에 대응되는 관세율 데이터

HsUsMapping
// HS Code ↔ US Code 매핑 정보
// 관세 조회 시 중간 연결 역할
```
DB 테이블이랑 1대1 매핑, Repository에서 직접 사용
# Controller Layer
```
AuthController
// 로그인 / 회원가입 API 처리

AuthPageController
// 로그인 / 회원가입 페이지(View) 반환

BookmarkController
// 북마크 추가 / 삭제 / 조회 요청 처리

DetailController
// HS Code 상세 정보 페이지 처리

HistoryController
// 사용자 검색 기록 조회

HomeController
// 메인 페이지 요청 처리

SearchController
// HS Code 검색 API 처리

TariffController
// 관세 정보 조회 API 처리
```
HTTP 요청의 진입 지점, 요청 파라미터 검증 및 응답 반환, 비즈니스 로직은 Service로 넘김

# Service Layer
```
UserService
// 회원 관련 비즈니스 로직
// 회원가입, 로그인 검증, 비밀번호 암호화

TariffService
// HS Code 검색 및 관세 조회 핵심 로직
// 여러 Repository를 조합하여 처리
```
핵심 비즈니스 로직 담당, Controller와 Repository 사이의 중재자 역할

# Repository Layer
```
UserRepository
// User 엔티티 DB 접근

HsCodeRepository
// HS Code 검색 및 조회

BookmarkRepository
// 북마크 저장 및 조회

HistoryRepository
// 검색 기록 관리

UsTariffRepository
// 미국 관세 정보 조회

HsUsMappingRepository
// HS ↔ US 매핑 정보 조회
```
DB 접근 전용 계층, SQL 대신 JPA 메서드 사용, 비즈니스 로직 없음

# DTO(Data Transfer Object)
```
HsCodeEntry
// HS Code 검색 결과 전달용 DTO

UsTariffResponse
// 관세 정보 응답 전용 DTO
```
Entity 직접 노출 방지를 위한 클래스, 필요한 데이터만 선별해서 전달, Controller 와 View 사이의 데이터 포맷

# Config Layer
```
SecurityConfig
// Spring Security 설정
// 인증 / 인가 정책 정의

PasswordConfig
// 비밀번호 암호화 설정
// PasswordEncoder Bean 등록
```
애플리케이션 전역 설정 담당, 보안 및 환경 설정 목적

# Loader Layer
```
HsUsMappingLoader
// HS ↔ US 매핑 데이터 초기 적재

UsTariffLoader
// 미국 관세 데이터 초기 적재
```
초기 데이터 DB 적재 목적이고 애플리케이션 초기 시작 시 실행

# Application Entry Point
```
HsCodeSearchApplication
// Spring Boot 애플리케이션 시작 클래스
// main() 메서드 포함
```
