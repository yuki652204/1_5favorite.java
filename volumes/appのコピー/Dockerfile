FROM eclipse-temurin:11-jdk

WORKDIR /app

COPY . .

# Mavenを使用する場合
RUN ./mvnw clean package -DskipTests

# Gradleを使用する場合は代わりにこちら
# RUN ./gradlew build -x test

# jarファイルを実行（ファイル名は実際のものに合わせてください）
CMD ["java", "-jar", "target/your-app-name.jar"]
