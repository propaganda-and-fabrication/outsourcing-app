# [Spring 5기] 아웃소싱 프로젝트 - README

## 프로젝트 개요

### 📌 프로젝트명: 배달 어플리케이션 개발

- **프로젝트 기간:** 1주일  
- **목표:** 배달 시장에 새롭게 진출하는 스타트업을 위한 배달 어플리케이션 백엔드 개발  
- **주요 기술 스택:**  
  - **Backend:** Spring Boot, JPA  
  - **Database:** MySQL (ERD 작성 포함)  
  - **Version Control:** Git, GitHub (브랜치 전략 적용)  
  - **Testing:** JUnit, Mockito (테스트 코드 작성 및 커버리지 30% 이상 달성 목표)  

## 프로젝트 개발 가이드

### 1️⃣ 프로젝트 초기 설정

- GitHub Repository 생성 및 팀원 초대  
- 프로젝트 요구사항 분석 및 주요 기능 정의  
- 기술 스택 및 개발 일정 확정  
- ERD 및 API 명세서 작성  

### 2️⃣ 개발 진행

- JPA를 활용한 데이터베이스 연동  
- 회원가입 및 로그인 기능 구현 (JWT 인증 적용)  
- 가게 및 메뉴 관리 기능 구현 (사장님 권한 부여)  
- 주문 및 리뷰 기능 개발 (고객 권한 적용)  
- 트러블 슈팅 기록 관리  

### 3️⃣ 테스트 및 버그 수정

- JUnit과 Mockito를 활용한 단위 테스트 작성  
- 테스트 코드 커버리지 30% 이상 달성  
- 실제 환경을 고려한 `@SpringBootTest` 적용  
- 발견된 버그 및 예외 처리 강화  

## 필수 구현 기능

### ✅ 공통 요구사항

- Git을 활용한 협업 (브랜치 전략 적용)  
- 테스트 코드 작성 및 최소 30% 커버리지 확보  
- ERD 및 API 명세 작성 후 개발 진행  

### ✅ 핵심 기능

- **회원 관리:** 회원가입, 로그인, 회원 탈퇴 (USER / OWNER 권한 분리)  
- **가게 관리:** 가게 등록, 수정, 삭제 (OWNER 전용)  
- **메뉴 관리:** 메뉴 추가, 수정, 삭제 (OWNER 전용)  
- **주문 기능:** 고객 주문 생성, 사장님 주문 수락 및 상태 변경  
- **리뷰 기능:** 고객 주문 완료 후 리뷰 작성 (별점 포함)  

## 도전 기능 (선택 사항)

### ✅ 구현된 도전 기능
- **이미지 저장 기능:**
  - 가게, 메뉴, 프로필 등의 이미지를 업로드할 수 있음
  - 이미지 저장소로 AWS S3 사용
  - 지원되는 이미지 파일 형식: jpg, png, jpeg
  - 예외 처리:
    - 허용되지 않는 파일 형식 업로드 제한
    - 파일 크기 제한 초과 시 업로드 불가

## ERD 및 API 명세서

- [ERD 설계]
  ![Image](https://github.com/user-attachments/assets/2656d313-df86-4997-bacb-0ab7507c114a)
  
- [API 명세서]
  ![Image](https://github.com/user-attachments/assets/3a38edd7-7d9c-48c5-9780-b612cf44baba)
  ![Image](https://github.com/user-attachments/assets/a72b075f-2772-4fff-8762-f850f28f4c99)
  ![Image](https://github.com/user-attachments/assets/d4446598-9e5c-40e9-9ce7-ef2353d59d10)

## 프로젝트 실행 방법

```bash
# 프로젝트 Clone
git clone https://github.com/propaganda-and-fabrication/outsourcing-app.git

# 환경 변수 설정
# .env 파일을 생성하고 필수 환경 변수를 추가합니다.

# 빌드 및 실행
./gradlew build
```

## 발표 준비

- [ ] 프로젝트 개요 및 핵심 기능 설명  
- [ ] 트러블 슈팅 사례 공유  
- [ ] 팀원 소감 및 피드백 정리  

---

본 프로젝트는 Spring Boot 기반으로 개발되었으며, 협업을 통해 실무와 유사한 환경에서 백엔드 개발을 경험하는 것을 목표로 합니다.

