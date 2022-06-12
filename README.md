# Demo project for building serverless REST APIs using AWS Lambda

This a sample project using Visual Studio Code remote containers for building and deploying AWS Lambda functions written in Java.

# Container configuration

It has been created starting from the [Microsoft example](https://github.com/microsoft/vscode-remote-try-java) and enhanced by me by adding:
 * AWS CDK 2.x (it will download the latest version at the moment of container creation, unless you specify something else)
 * AWS SAM 1.x (same as above)
 * AWS CLI 2.x (same as above)

 For example:
 ```
vscode ➜ /workspaces/hello-aws-lambda-java (main ✗) $ cdk --version
2.27.0 (build 8e89048)
vscode ➜ /workspaces/hello-aws-lambda-java (main ✗) $ sam --version
SAM CLI, version 1.51.0
vscode ➜ /workspaces/hello-aws-lambda-java (main ✗) $ java --version
openjdk 17.0.2 2022-01-18 LTS
OpenJDK Runtime Environment Microsoft-30338 (build 17.0.2+8-LTS)
OpenJDK 64-Bit Server VM Microsoft-30338 (build 17.0.2+8-LTS, mixed mode, sharing)
vscode ➜ /workspaces/hello-aws-lambda-java (main ✗) $ aws --version
aws-cli/2.7.6 Python/3.9.11 Linux/5.10.16.3-microsoft-standard-WSL2 exe/x86_64.debian.11 prompt/off
vscode ➜ /workspaces/hello-aws-lambda-java (main ✗) $ 
```

In order to better suite my development use-cases, the local Git and AWS configurations are mounted inside the container so that they are available and ready for use.