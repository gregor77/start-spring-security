# spring security start project

## 실습
### 5.1. Authentication
#### 5.1.2 Password Storage

* src : PasswordEncoder.java
* [왜 password hasing시 Bcrypt가 추천되어질까](https://velog.io/@kylexid/왜-bcrypt-암호화-방식이-추천되어질까)

### 5.2. Protection Against Exploits (악용에 대한 보호)
#### 5.2.1. CSRF (Cross Site Request Forgery : 크로스 사이트 요청 위조)
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

## 참고
*  [spring security docs](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)