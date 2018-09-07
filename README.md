# To-Do Demo Project

To-Do Demo Project : Spring Boot + JPA + H2 + Angular 6 + SemanticUI

Spring MVC 패턴을 사용하여 RESTful API로 백엔드를 구성하였으며,

Angular 6 + Semantic UI 로 프론트엔드를 구성함(프론트엔트 소스는 별도 github에 있으며, 해당 소스에는 빌드된 리소스만 존재함)

* 사용자는 텍스트로 된 할일을 추가할 수 있다. 
  * 할일 추가 시 다른 할일들을 참조 걸 수 있다.
    * 미완료된 할일 추가 시, 참조하는 할일 모두 미완료여야 한다.
    * 완료된 할일 추가 시, 참조하는 할일에 대한 제약이 없다.
  * 참조는 다른 할일의 id를 명시하는 형태로 구현한다. (예시 참고)
* 사용자는 할일을 수정할 수 있다.
  * 미완료 수정인 경우, 참조하는 할일 모두 미완료여야 한다.
  * 완료 수정인 경우, 참조된 할일 모두 완료여야 한다.
  * 참조 수정 시, 순환참조/셀프참조가 방지되어야 한다.
* 사용자는 할일 목록을 조회할 수 있다.
  * 조회시 작성일, 최종수정일, 내용이 조회 가능하다.
  * 할일 목록은 페이징 기능이 있다.
* 사용자는 할일을 완료처리 할 수 있다.
  * 완료처리 시 참조가 걸린 완료되지 않은 할일이 있다면 완료처리할 수 없다. (예시 참고)



## Prerequisites

JDK 1.8 이상

## Test

```
1. UnitTest : ./gradlew test
2. IntegrationTest : ./gradlew integrationTest
3. WholeTest : ./gradlew wholeTest
```

## Build

```
./gradlew build
```

## Run

빌드를 수행한 이후에

```
cd ./build/libs
java -jar todo-1.0.0-RELEASE.jar
```

혹은 프로젝트 루트 폴더에서

```
./gradlew bootRun
```

브라우저에서 접속

```
http://localhost:8080
```

## Authors

Chanung Yun(cu.sonar@gmail.com)

## License

This project is licensed under the MIT License

## Dependencies

* Spring Boot 2.0.4
* Lombok 1.16.22
* H2 1.4.197
