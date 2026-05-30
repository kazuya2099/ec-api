plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.4.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

// ─────────────────────────────────────────────
// Spotless: コードフォーマット設定
//   ./gradlew spotlessApply  → 自動整形
//   ./gradlew spotlessCheck  → フォーマット確認（CIで使用）
// ─────────────────────────────────────────────
spotless {
    java {
        // Google Java Format でコード整形
        googleJavaFormat("1.25.0").aosp()
        // import を自動削除
        removeUnusedImports()
        // 末尾空白を削除
        trimTrailingWhitespace()
        // ファイル末尾に改行を追加
        endWithNewline()
        // ライセンスヘッダー（任意）
        // licenseHeader("/* (C) 2025 Example Corp */")
    }
    // Kotlin DSL ビルドファイルも整形対象
    kotlinGradle {
        ktlint("1.5.0")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // ─── Web ───────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // ─── Hibernate / JPA ───────────────────────
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-core")

    // ─── Validation ────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    // ─── PostgreSQL Driver ──────────────────────
    runtimeOnly("org.postgresql:postgresql")

    // ─── Flyway ───
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")

    // ─── Lombok ────────────────────────────────
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ─── Test ──────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ビルド前に Spotless チェックを実行（CI向け）
tasks.named("check") { dependsOn("spotlessCheck") }

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "failed", "skipped")  // ← 追加
    }
}