# Spring Security와 JWT를 활용한 인증/인가 구현

~~매번 구현하기 귀찮아서 만들어 올리는 스니펫 코드~~

이 프로젝트는 Spring Security와 JWT(JSON Web Token)를 활용하여 안전한 인증 및 인가 시스템을 구현한 예제입니다.


## 기능 구현

### 1. 인증 (Authentication)
- 이메일/패스워드 기반 로그인
- JWT 토큰 발급 및 관리
- 회원가입 기능

### 2. 인가 (Authorization)
- Role 기반 접근 제어 (MEMBER, ADMIN)
- 보호된 API 엔드포인트
- JWT 토큰을 통한 사용자 식별 및 권한 검증

## 주요 컴포넌트

### 엔티티
- `Member`: 사용자 정보를 저장하는 엔티티
- `Role`: 사용자 권한을 나타내는 열거형 (MEMBER, ADMIN)

### 인증/인가 관련 클래스
- `SecurityConfig`: Spring Security 설정
- `JwtUtil`: JWT 토큰 생성, 검증 및 관리
- `JwtAuthenticationFilter`: 요청 헤더에서 JWT를 추출하여 인증 처리
- `CustomUserDetails`: Spring Security의 UserDetails 인터페이스 구현
- `CustomUserDetailsService`: 사용자 정보를 로드하기 위한 서비스

### API 구조
- `/auth/register`: 회원가입 API (POST)
- `/auth/login`: 로그인 및 JWT 토큰 발급 API (POST)
- `/auth/me`: 현재 로그인한 사용자 정보 조회 API (GET, 인증 필요)

## API 사용 예시

### 회원가입
```http
POST /auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "userPassword",
  "role": "MEMBER"
}
```

### 로그인
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "userPassword"
}
```

응답:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 인증이 필요한 API 호출
```http
GET /auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 인증 흐름

1. 사용자가 이메일과 비밀번호로 로그인 요청
2. 서버는 사용자 정보를 검증하고 JWT 토큰 발급
3. 클라이언트는 받은 JWT 토큰을 요청 헤더에 포함하여 API 호출
4. JwtAuthenticationFilter가 토큰을 검증하고 사용자 인증 처리
5. SecurityConfig에 설정된 권한에 따라 API 접근 허용/거부
