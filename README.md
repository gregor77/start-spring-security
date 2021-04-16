# Spring Security start project

## 실습
## 5.1. Authentication
### 5.1.2 Password Storage

* src : PasswordEncoder.java
* [왜 password hasing시 Bcrypt가 추천되어질까](https://velog.io/@kylexid/왜-bcrypt-암호화-방식이-추천되어질까)

## 5.2. Protection Against Exploits (악용에 대한 보호)
### 5.2.1. CSRF (Cross Site Request Forgery : 크로스 사이트 요청 위조)
공격자가, 사용자(즉 희생자)가 사용하고 있는 Web Browser를 통해서, 공격자가 조작한 HTTP request를, Web Server에게 보내는 attack(공격) 입니다.
Attack에 성공하려면 공격자는, 사용자의 Web Browser가 “조작된 HTTP request”를 보내도록 유도해야 합니다
(예를 들면, Social engineering 기법을 사용한 email을 보내어, 첨부된 URL 주소 또는 hyperlink를 클릭하도록 유도).

* XSS Atack
  - PC에서 악성 Script가 실행되어 지는 것

* CSRF Attack
  - Browser에서 이미 접속한 Web Application 에게, "위조된 HTTP Request"와 같은 "Browser 사용자가  원하지 않는 action"을 보내어,
 Web server로 위조된 request 수행
  - **예방법 : 인증 정보를 request 양식에 포함시켜, Web Server에서 HTTP request를 받아들일 때, source를 확인하여 
  “다른 Session(인증 받지 아니한 location)”에서 온 Request이면 reject 수행**

#### Sprint security csrf 설정
postman을 활용하여 API를 직접 테스트해도 무방하다.
정상 케이스를 테스트하려면, cookie에 있는 csrf 토큰을 header에 추가하여 요청보내면 된다.

1. WebSecurityConfig에서 csrf 설정 추가
    ```java
    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf()
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    } 
    ```
2. csrf 설정을 추가하면 리소스를 변경하는 HTTP Method(POST, PUT, DELETE) 를 사용하는 경우,
유효한 csrf token과 함께 요청하지 않으면 Forbidden(403) 상태코드를 리턴한다.
* UserControllerTest 에 GET, POST 메소드의 테스트케이스 참고

#### csrf 화면 테스트
* 브라우저 입력 주소 : http://localhost:8080/login.html
* form 로그인시, 개발자 브라우저로 보면 _csrf input이 hidden으로 생성되어있는 것을 확인할 수 있다.
* WebSecurityConfig에서 csrf()를 비활성화하면, hidden 타입의 _csrf input 필드가 사라진다.

## 참고
* [spring security web example](https://spring.io/guides/gs/securing-web/)
* [spring security docs](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
* [spring security test 블로그](https://dongdd.tistory.com/175)