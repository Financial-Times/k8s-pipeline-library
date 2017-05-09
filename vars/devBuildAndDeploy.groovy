import com.ft.up.DeploymentUtils
import com.ft.up.DevBuildConfig
import com.ft.up.DockerUtils

def call(DevBuildConfig config) {

  def deployUtil = new DeploymentUtils()

  node('docker') {
    try {
      stage('checkout') {
        checkout scm
      }

      String imageVersion = deployUtil.getFeatureName(env.BRANCH_NAME)
      stage('build image') {
        DockerUtils dockerUtils = new DockerUtils()
        def image = dockerUtils.buildImage("${config.appDockerImageId}:${imageVersion}")
        dockerUtils.pushImageToDH(image)
      }

      String env = deployUtil.getEnvironment(env.BRANCH_NAME)
      //  todo [sb] handle the case when the environment is not specified in the branch name

      stage("deploy to ${env}") {
        deployUtil.deployAppWithHelm(imageVersion, env)
      }
    }
    finally {
      cleanWs()
    }
  }

}






