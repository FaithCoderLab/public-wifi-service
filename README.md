# 공공 WiFi 정보 제공 서비스

서울시 공공 WiFi 위치 정보를 제공하는 웹 서비스입니다.

## 기술 스택

- Java 8
- JSP (Jakarta EE 8)
- SQLite
- Maven
- Tomcat 9
- OkHttp3 (HTTP Client)
- Gson (JSON 처리)
- Lombok

## 주요 기능

- 공공 WiFi 정보 조회
- 내 위치 기반 주변 WiFi 정보 제공 (가까운 순 20개)
- 위치 히스토리 저장 및 조회
- 북마크 그룹 관리
- WiFi 정보 북마크 기능

## 프로젝트 구조

```
src/
├── main/
│   ├── java/         # Java 소스 코드
│   ├── resources/    # 설정 파일 및 리소스
│   └── webapp/       # 웹 리소스 (JSP, CSS, JS)
└── test/             # 테스트 코드
```

## 시작하기

### 필수 조건

- JDK 1.8
- Apache Tomcat 9
- IntelliJ IDEA Ultimate

### 설치 및 실행

1. 프로젝트 클론
```bash
git clone [repository-url]
```

2. Maven 의존성 설치
```bash
mvn clean install
```

3. Tomcat 서버 설정 및 실행

4. 브라우저에서 접속
```
http://localhost:8080
```

## 라이센스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details