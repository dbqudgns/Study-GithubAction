# Github Actions CI/CD

학습 기간: 2025/04/05 ~ 2025/04/10

## CI (Continuous Integration) : 지속적인 통합

- 여러 개발자의 코드를 자동으로 빌드하고 테스트해서 통합하는 프로세스

## CD (Continuous Deployment) : 지속적인 배포

- 테스트를 통과한 코드를 자동으로 배포하는 프로세스

---

=> Github Actions을 사용해서 AWS EC2 서버에 Docker Compose를 이용해 웹 애플리케이션을 자동으로 빌드하고 배포하도록 설정

```
name: Backend budtree_app

on:
  push:
    branches:
      - 'master'         # master 브랜치로 push할 때 실행
      - 'feat/**'        # feat/** 브랜치로 push할 때 실행
  pull_request:
    branches:
      - 'master'        # master 브랜치로 PR 생성 시 실행
      - 'develop'       # develop 브랜치로 PR 생성 시 실행

# Github 저장소의 내용을 읽을 수 있는 권한 부여
permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest # 최신 Ubuntu 서버에서 실행

    steps:
      - name: GitHub 저장소의 최신 코드 내려받기
        uses: actions/checkout@v3

      - name: Java 17 버전 설치 및 사용
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin' # 안정적인 OpenJDK 배포판

      - name: Gradlew Build를 위한 gradlew 스크립트에 실행 권한 부여
        run: chmod +x gradlew

      - name: 테스트 코드를 제외하고 웹 애플리케이션 빌드 실행
        run: ./gradlew build -x test

      - name: Docker hub에 로그인
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Docker 이미지 생성 및 Docker hub에 업로드
        run: |
          docker build -t ${{ secrets.DOCKER_USERNAME }}/budtree_app:latest .
          docker push ${{ secrets.DOCKER_USERNAME }}/budtree_app:latest

      - name: EC2 서버에 SSH로 접속해 기존 컨테이너 종료 및 최신 이미지를 받아 재배포
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.AWS_HOST }}
          username: ubuntu
          key: ${{ secrets.AWS_SECRET_KEY }}
          port: 22
          script: |
            cd /home/ubuntu/compose
            docker compose down
            docker pull ${{ secrets.DOCKER_USERNAME }}/budtree_app:latest
            docker compose up --build -d
            docker image prune -f
```
