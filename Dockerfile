# Build stage: JDK 24 + Maven
FROM eclipse-temurin:24-jdk AS build

WORKDIR /app

# Instaliraj Maven
RUN apt-get update && apt-get install -y maven

# Kopiraj ceo projekat
COPY . .

# Build svih modula i kreiraj uber jar
RUN mvn clean package -DskipTests

# Runtime stage: JDK 24 + GUI + pcscd
FROM eclipse-temurin:24-jdk

WORKDIR /app

# Instaliraj GUI i smart card pakete
RUN apt-get update && apt-get install -y \
    libxext6 libxrender1 libxtst6 libxi6 \
    pcscd pcsc-tools libccid \
    && rm -rf /var/lib/apt/lists/*

# Kopiraj uber jar iz build stage
COPY --from=build /app/eid_viewer/target/eid_viewer-*.jar /app/eid_viewer.jar

# Startuj pcscd u foreground-u i GUI aplikaciju
# ne pokreći pcscd ovde — koristi hostov daemon preko /var/run/pcscd
ENTRYPOINT ["sh","-c","java -jar /app/eid_viewer.jar"]