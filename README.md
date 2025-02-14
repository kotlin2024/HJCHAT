# HJ-Chat

HJ-Chat은 실시간 채팅 기능을 제공하는 웹 애플리케이션으로, WebSocket을 활용하여 빠르고 안정적인 메시지 전송이 가능합니다. 또한 Kafka를 이용한 메시지 브로커 구조를 적용하여 확장성과 안정성을 고려한 설계를 채택하였습니다.

## 🌟 주요 기능

- **실시간 채팅** (WebSocket 기반)
- **Kafka 기반 메시지 브로커** (토픽 및 구독 방식)
- **공개 채팅방 생성**
- **비공개 채팅방 생성 및 초대 기능**
- **사용자 인증**
  - 로그인, 회원가입, 이메일 인증 (SMTP)
- **Redis 기반 Refresh Token 블랙리스트 관리**
- **사용자 프로필 관리**
  - 프로필 조회
  - AWS S3 Presigned URL을 활용한 프로필 이미지 업로드
- **친구 관리 기능**
  - 친구 추가 요청 및 승인/거절
  - 친구 삭제

## 🏗️ 기술 스택

### 📌 백엔드
- **언어**: Kotlin
- **프레임워크**: Spring Boot (version 3.4.0)
- **실시간 통신**: WebSocket
- **메시지 브로커**: Apache Kafka
- **데이터베이스**: MySQL (AWS RDS)
- **토큰 관리**: Redis
- **파일 스토리지**: AWS S3
- **이메일 인증**: SMTP
- **보안**: JWT 기반 인증

### ☁️ 인프라
- **서버**: AWS EC2
- **도메인 관리**: AWS Route 53
- **로드 밸런서 및 HTTPS 설정**
- **배포 방식**: Docker + Docker Compose
- **CI/CD**: jenkins

## 🚀 배포 및 실행 방법

### 1️⃣ Docker Compose build 

```sh
docker-compose build --no-cache
```

### 2️⃣ Docker Compose로 실행

```sh
docker-compose up -d
```

### 3️⃣ 서버 접속

로드 밸런서를 통해 HTTPS가 설정된 상태에서 `https://hj-chat.com`으로 접속합니다.
http로 접속하면 자동으로 https로 리디렉션 됩니다.

##  테스트 계정

- ID: test5  pw: 1234
- ID: test6  pw: 1234
- ID: test7  pw: 1234
- ID: test8  pw: 1234
- ID: test9  pw: 1234



## 📌 API 명세

API 명세는 Swagger 또는 Postman 문서를 참고해주세요.

- **Swagger URL**: [`[https://hj-chat.com/swagger-ui](https://api.hj-chat.com/swagger-ui/index.html#/)`]
