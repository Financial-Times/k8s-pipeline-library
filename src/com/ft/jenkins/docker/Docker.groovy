package com.ft.jenkins.docker

import static DockerConstants.DOCKERHUB_CREDENTIALS
import static DockerConstants.DOCKERHUB_URL
import static DockerConstants.FT_DOCKER_REGISTRY_CREDENTIALS
import static DockerConstants.FT_DOCKER_REGISTRY_NAME


final class DockerConstants {
  public static final String DOCKERHUB_CREDENTIALS = "ft.dh.credentials"
  public static final String DOCKERHUB_URL = "" // For Jenkins to connect to Dockerhub, it needs the URL empty.
  public static final String FT_DOCKER_REGISTRY_NAME = "nexus.in.ft.com:5000"
  public static final String FT_DOCKER_REGISTRY_CREDENTIALS = "ft.docker_internal.credentials"
}

void pushImageToDockerReg(image, String dockerRegistryUrl, String credentials = null) {
  docker.withRegistry(dockerRegistryUrl, credentials) {
    image.push()
    image.push("latest")
  }
}

void pullImageFromDockerReg(String tag) {
  boolean useInternalDockerReg = tag.startsWith(FT_DOCKER_REGISTRY_NAME)
  String credentials = useInternalDockerReg ? FT_DOCKER_REGISTRY_CREDENTIALS : DOCKERHUB_CREDENTIALS
  String registry = useInternalDockerReg ? FT_DOCKER_REGISTRY_CREDENTIALS :  DOCKERHUB_URL
  echo "Pulling image ${tag} from registry ${registry}"
  docker.withRegistry(registry, credentials) {
    docker.image(tag).pull()
  }
}

def buildImage(String dockerTag, String folder = ".") {
  def image = null
  /*  adding the internal nexus credentials to support the maven apps connecting to Nexus */
  withCredentials([
          nexusCredentialsEnvVars(),
          githubTokenCredentialsEnvVars()
  ]) {
    image = docker.build(dockerTag,
            "--build-arg SONATYPE_USER=${env.NEXUS_USERNAME} --build-arg SONATYPE_PASSWORD=${env.NEXUS_PASSWORD} --build-arg GITHUB_USERNAME=${env.GITHUB_USERNAME} --build-arg GITHUB_TOKEN=${env.GITHUB_TOKEN} ${folder}")
  }
  image
}

private void githubTokenCredentialsEnvVars() {
  usernamePassword(
          credentialsId: "github.token.credentials",
          usernameVariable: "GITHUB_USERNAME",
          passwordVariable: "GITHUB_TOKEN"
  )
}

private void nexusCredentialsEnvVars() {
  usernamePassword(
          credentialsId: "nexus.credentials",
          passwordVariable: 'NEXUS_PASSWORD',
          usernameVariable: 'NEXUS_USERNAME'
  )
}

void buildAndPushImage(String dockerTag) {
  lock(dockerTag) {
    if (imageExists(dockerTag)) {
      /*  do not overwrite */
      echo "Docker image ${dockerTag} already exists. Skip building it ..."
      return
    }

    def image = buildImage(dockerTag)
    boolean useInternalDockerReg = dockerTag.startsWith(FT_DOCKER_REGISTRY_NAME)
    if (useInternalDockerReg) {
      pushImageToDockerReg(image, "https://${FT_DOCKER_REGISTRY_NAME}", FT_DOCKER_REGISTRY_CREDENTIALS)
    } else {
      pushImageToDockerReg(image, DOCKERHUB_URL, DOCKERHUB_CREDENTIALS)
    }
    /*  remove the image after push */
    sh "docker rmi ${image.id}"
  }
}

private boolean imageExists(String tag) {
  try {
    pullImageFromDockerReg(tag)
    return true
  } catch (ignored) {
    return false
  }
}

return Docker // We're returning the script in order to allow it to be loaded in a variable and executed on demand (check DockerUtilsTest for an example)
