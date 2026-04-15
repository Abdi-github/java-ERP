# ──────────────────────────────────────────────────────────
# SwiftApp ERP — Multi-Stage Dockerfile
# ──────────────────────────────────────────────────────────
# Stage 1: Build the application with Maven
# Stage 2: Extract Spring Boot layered JAR
# Stage 3: Minimal JRE runtime image
# ──────────────────────────────────────────────────────────

# ════════════════════════════════════════════════════════════
# STAGE 1 — Builder: Compile and package with Maven
# ════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# ── Copy Maven wrapper and POM first (for dependency caching) ──
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# ── Download all dependencies (cached unless pom.xml changes) ──
RUN ./mvnw dependency:go-offline -B -q

# ── Copy source code ──
COPY src/ src/

# ── Build the application (skip tests — they run in CI) ──
RUN ./mvnw package -DskipTests -B -q \
    && mv target/*.jar target/app.jar

# ════════════════════════════════════════════════════════════
# STAGE 2 — Layers: Extract Spring Boot layered JAR
# ════════════════════════════════════════════════════════════
# STAGE 2 — Runtime: Minimal JRE image
# ════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine AS runtime

# ── Metadata ──
LABEL maintainer="SwiftApp ERP <dev@swiftapp.ch>"
LABEL description="SwiftApp ERP — Swiss Watch Manufacturing & Retail ERP"
LABEL version="0.0.1-SNAPSHOT"

# ── Install curl for health checks ──
RUN apk add --no-cache curl tzdata \
    && cp /usr/share/zoneinfo/Europe/Zurich /etc/localtime \
    && echo "Europe/Zurich" > /etc/timezone \
    && apk del tzdata

# ── Create non-root user ──
RUN addgroup -g 1000 -S appgroup \
    && adduser -u 1000 -S appuser -G appgroup

# ── Application directory ──
WORKDIR /app

# ── Copy application JAR ──
COPY --from=builder /build/target/app.jar ./app.jar

# ── Set ownership ──
RUN chown -R appuser:appgroup /app

# ── Switch to non-root user ──
USER appuser:appgroup

# ── Expose application port ──
EXPOSE 8080

# ── JVM container-aware tuning ──
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=Europe/Zurich"

# ── Health check ──
HEALTHCHECK --interval=30s --timeout=10s --start_period=60s --retries=3 \
    CMD curl -sf http://127.0.0.1:8080/actuator/health || exit 1

# ── Entrypoint ──
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

