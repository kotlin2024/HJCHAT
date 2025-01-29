plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    kotlin("plugin.noarg") version "1.9.25"
    kotlin("plugin.allopen") version "1.9.25"
}

group = "HJP"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}
val queryDslVersion = "5.0.0"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // GraphQL
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:11.0.0")
    implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:11.0.0")


    // SWAGGER
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // MYSQL
    implementation("mysql:mysql-connector-java:8.0.33")


    // JJWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    //QueryDsl
    implementation("com.querydsl:querydsl-jpa:$queryDslVersion:jakarta")
    kapt("com.querydsl:querydsl-apt:$queryDslVersion:jakarta")

    //SMTP
    implementation ("org.springframework.boot:spring-boot-starter-mail")

    //Web Socket
    implementation ("org.springframework.boot:spring-boot-starter-websocket")

    //log
    implementation ("ch.qos.logback:logback-classic:1.4.5")  // 최신 버전 사용

    //Kafka
    implementation("org.springframework.kafka:spring-kafka")

    //AWS SDK
    implementation("software.amazon.awssdk:s3:2.20.92")

    // AWS CLOUD 의존성
    implementation("io.awspring.cloud:spring-cloud-starter-aws:2.4.2")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
