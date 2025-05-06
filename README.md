# DevopsProject
For Devops Learning Purposes
# CI/CD Pipeline for glearning-order Microservice

This project implements a CI/CD pipeline that builds a Docker image for the `devops-project` microservice using GitHub Actions and pushes it to Amazon ECR. The setup uses OpenID Connect (OIDC) to securely authenticate GitHub Actions with AWS.

---

## 📦 Pipeline Design

The GitHub Actions pipeline is triggered on every push to the `main` branch. Here's what it does step-by-step:

1. **Checkout Code**: Uses `actions/checkout@v4` to clone the repository.
2. **Set Up Java**: Configures Java 17 using `actions/setup-java`.
3. **Build JAR with Maven**: Executes `mvn clean package -DskipTests` to compile the project.
4. **Assume IAM Role**: Uses OIDC to securely authenticate with AWS and assume a predefined IAM role.
5. **Login to ECR**: Authenticates Docker with Amazon ECR.
6. **Build and Push Docker Image**:
   - Builds the image using a multi-stage Dockerfile.
   - Tags it using the ECR repository URI.
   - Pushes the image to ECR.

---

## 🔐 IAM Role Setup

To securely allow GitHub Actions to push Docker images to Amazon ECR, we created an IAM role with the following configuration:

### Trust Policy
This policy allows GitHub Actions to assume the IAM role using OIDC:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<account-id>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:sanjana70/*"
        }
      }
    }
  ]
}
```

### Permissions Policy
The role also has permissions to push images to ECR:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:PutImage",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload"
      ],
      "Resource": "*"
    }
  ]
}
```

---

## 🐳 How Multi-Stage Build Works

The project uses a multi-stage Dockerfile to keep the final image small and clean:

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Benefits:
- Keeps the final image lightweight by excluding Maven and source files.
- Improves security and performance.
- Faster deployment time.

---
