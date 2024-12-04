# 기획 및 설계

이러한 문제들을 방지하기 위해 결제 API에 대한 충분한 학습과 테스트, 그리고 보안 관련 지식을 쌓을 것임

### 사용자 테스트

**이름**: 김세은

**이메일**: [rlatpdms1014@gmail.com](mailto:rlatpdms1014@gmail.com)

**전화번호**: 01099368036

### **< 사용 기술 >**

| 기술 분류 | 사용 기술 |
| --- | --- |
| Backend | Spring Boot (Web, JPA, Security) |
| Frontend | Thymeleaf |
| Database | H2 Database, Spring Data JPA |
| File Handling | Apache Commons FileUpload, Commons IO, Apache Tika |
| Dynamic Queries | QueryDSL |
| Build Tool | Gradle |

### **< 구현 범위 >**

- **회원가입 및 로그인/로그아웃**
    - 입력 필드 예외 처리
    - 사용자 권한 분리(USER, ADMIN): 관리자 계정으로 게시글 삭제 가능
        - ADMIN Id: admin
        - ADMIN Pw: 1014
    - 로그인이 필요한 경로 진입 시 로그인 화면으로 리다이렉트
        - 로그인이 필요한 경로: /post/**
        - 필요하지 않은 경로: 그 외 및 /post/list
- **게시글 작성, 수정, 삭제 및 조**회
    - 게시글 구성요소: 제목, 내용, 작성자 Id, 작성일, 조회수, 파일
    - 작성자만 게시글 수정이 가능
    - 관리자 계정으로 모든 게시글 삭제 가능
    - 제목이나 작성자 Id로 게시글 검색 가능
    - 한 페이지 당 게시글 10개 단위로 페이징
- **파일 업로드, 수정, 삭제 및 다운로드**
    - 업로드 가능한 파일 제한
        - Jpeg, Jpg, Png, Mp4, Mp3, Pdf, ppt, doc, xsl, txt 등 업로드 가능
    - 실제로 저장되는 이름과 보여지는 이름, 다운로드 되는 이름을 다르게 함
    - 파일 수정 시 기존 업로드 되어 있던 파일 삭제 됨 (실제 파일 저장 경로에서 삭제되는 것은 아님)