# See here for image contents: https://github.com/microsoft/vscode-dev-containers/blob/main/containers/java/.devcontainer/base.Dockerfile
# [Choice] Java version (use -bullseye variants on local arm64/Apple Silicon): 8, 11, 17, 8-bullseye, 11-bullseye, 17-bullseye, 8-buster, 11-buster, 17-buster
ARG VARIANT=11
FROM mcr.microsoft.com/vscode/devcontainers/java:${VARIANT}

# [Option] Install Maven
ARG INSTALL_MAVEN="false"
ARG MAVEN_VERSION=""
# [Option] Install Gradle
ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""
RUN if [ "${INSTALL_MAVEN}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; fi

# [Choice] Node.js version: none, lts/*, 16, 14, 12, 10
ARG NODE_VERSION="none"
RUN if [ "${NODE_VERSION}" != "none" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# AWS CLI is already installed as remote container feature - we add AWS CDK and AWS SAM to the group
ARG AWS_CDK_VERSION="none"
RUN if [ "${AWS_CDK_VERSION}" != "none" ]; then su vscode -c "umask 0002 && npm install -g aws-cdk@${AWS_CDK_VERSION}"; fi

ARG AWS_SAM_VERSION="none"
RUN if [ "${AWS_SAM_VERSION}" != "none" ]; then \
        su vscode -c "umask 0002 && curl -o /tmp/aws-sam-cli-linux-x86_64.zip -L https://github.com/aws/aws-sam-cli/releases/${AWS_SAM_VERSION}/download/aws-sam-cli-linux-x86_64.zip" \
        && su vscode -c "umask 0002 && cd /tmp && unzip aws-sam-cli-linux-x86_64.zip -d sam-installation" \
        && su vscode -c "umask 0002 && sudo /tmp/sam-installation/install" \
        && su vscode -c "rm -rf /tmp/aws-sam-cli-linux-x86_64.zip sam-installation 2>&1"; \
    fi

# We install the docker client executable in this container and, in order to make it connect to the docker daemon,
# we also mount the socker  /var/run/docker.sock (in devcontainer.json) while ensuring that the local user ($USERNAME)
# is also part of this new 'docker' group (on my machine the group id is 1001 and should be the same between this container
# and the host machine)
ARG LOCAL_DOCKER_VERSION="none"
RUN if [ "${LOCAL_DOCKER_VERSION}" != "none" ]; then \
        su vscode -c "umask 0002 && curl -o /tmp/docker-bundle.tgz -L https://download.docker.com/linux/static/stable/x86_64/docker-${LOCAL_DOCKER_VERSION}.tgz" \
        && su vscode -c "umask 0002 && cd /tmp && tar xzf docker-bundle.tgz" \
        && su vscode -c "umask 0002 && sudo chown root:root /tmp/docker/docker && sudo mv /tmp/docker/docker /usr/local/bin" \
        && su vscode -c "rm -rf /tmp/docker 2>&1" \
        && su vscode -c "sudo groupadd docker -g 1001" \
        && su vscode -c "sudo usermod -a -G docker vscode"; \
    fi

ARG LOCAL_DOCKER_COMPOSE_VERSION="none"
RUN if [ "${LOCAL_DOCKER_COMPOSE_VERSION}" != "none" ]; then \
        su vscode -c "umask 0002 && curl -L https://github.com/docker/compose/releases/download/v${LOCAL_DOCKER_COMPOSE_VERSION}/docker-compose-linux-x86_64 -o /tmp/docker-compose" \
        && su vscode -c "umask 0002 && sudo chmod +x /tmp/docker-compose && sudo chown root:root /tmp/docker-compose" \
        && su vscode -c "umask 0002 && sudo mv /tmp/docker-compose /usr/local/bin"; \
    fi

# [Optional] Uncomment this section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
     && apt-get -y install --no-install-recommends python3-pip

# Required for access to AWS CodeCommit repositories using codecommit://<profile>@<repo-name> syntax
RUN su vscode -c "sudo pip install git-remote-codecommit"

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1
