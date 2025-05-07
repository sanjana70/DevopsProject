
# DEVOPS PROJECT - Secure CI Pipeline for Java Application with Multi-stage Docker Build and AWS OIDC Integration
Sanjana Suresh RA2412062015004

## Objective

This project implements a secure and automated CI/CD pipeline that:

- Pulls a Java application's source code.
- Builds a Docker image using a multi-stage Dockerfile.
- Authenticates with AWS using GitHub OpenID Connect (OIDC) (without access keys).
- Pushes the final Docker image to a private AWS Elastic Container Registry (ECR) repository.

## Repository Structure

```
devops-project/
├── .github/workflows/build-and-push.yml     # GitHub Actions workflow file
├── src/                                     # Java source code
├── Dockerfile                               # Multi-stage Dockerfile
├── pom.xml                                  # Maven configuration
├── README.md                                # Documentation
└── Required Screenshots                     #Required Screenshots to show project completion
```

## Pipeline Design

The GitHub Actions workflow (`.github/workflows/build-and-push.yml`) performs the following steps:

1. **Checkout**: Clones the `devops-project` repository.
2. **OIDC Authentication**: Assumes an AWS IAM role using GitHub's OIDC token (via `aws-actions/configure-aws-credentials@v2`).
3. **Docker Login**: Authenticates to Amazon ECR using the AWS CLI.
4. **Build and Push**:
   - Builds the Docker image using a multi-stage Dockerfile.
   - Tags the image with the `latest` tag.
   - Pushes the image to the AWS ECR private repository.

## IAM Role Setup

The IAM role was created in AWS with the following key configurations:

- **OIDC Trust Policy**: Allows GitHub's identity provider (`token.actions.githubusercontent.com`) to assume the role.
- **Permissions Policy**:
  - A minimal custom policy was used to follow the principle of least privilege:

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

## Multi-Stage Build Explained

The `Dockerfile` follows a two-stage build:

1. **Build Stage**:
   - Uses a Maven image to compile the Java source code into a `.jar` file.
2. **Final Stage**:
   - Uses a lightweight OpenJDK image to run the compiled `.jar`, keeping the final image minimal and secure.

This approach improves build efficiency and results in smaller production images.

## Challenges Faced

- Setting up GitHub OIDC correctly with the IAM trust policy format and conditions.
- Ensuring proper Maven build behavior within Docker, especially classpath and dependencies.
- Accidentally forgot to include the java distribution name
- Troubleshooting GitHub Actions errors when assuming the IAM role.
- Accidentaly gave the wrong data center name, causing build to fail
- Initially gave fullregistry access, then reduced the permissions through custom policy

## Screenshots

Please refer to the below screenshots:

1. **GitHub Actions Successful Run**  
   <img width="1349" alt="image" src="https://github.com/user-attachments/assets/6f88bd6d-5557-4cc4-b32b-999357f68386" />

2. **Docker Image in AWS ECR**  
   <img width="1349" alt="image" src="https://github.com/user-attachments/assets/cdfb3314-0ba7-4c3c-a371-1015a0faefc7" />

3. **AWS IAM Role Trust Policy**  
   ![image](https://github.com/user-attachments/assets/173c138a-d978-4ed5-9459-d01c43777753)


## Final Notes

This project adheres to DevOps best practices by:

- Avoiding the use of static AWS credentials.
- Using lightweight, production-ready Docker images.
- Keeping the build and deployment process entirely automated and reproducible.
- Using minimal permissions
