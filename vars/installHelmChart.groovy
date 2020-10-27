import com.ft.jenkins.JenkinsParams
import com.ft.jenkins.appconfigs.AppConfig
import com.ft.jenkins.cluster.*
import com.ft.jenkins.deployment.Deployments
import com.ft.jenkins.notification.Notifications
import com.google.common.collect.Multimap

/*
* Implementation of a generic pipeline that installs a helm chart in an environment.
**/

def call() {
  /*  determine parameter values */
  JenkinsParams jenkinsParams = new JenkinsParams()

  Environment targetEnv = computeTargetEnvironment()
  String targetEnvNamespace = params.Namespace
  String chartName = jenkinsParams.getRequiredParameterValue("Chart")
  String chartVersion = jenkinsParams.getRequiredParameterValue("Version")
  ClusterType deployOnlyInCluster = computeDeployOnlyInCluster()
  Region deployOnlyInRegion = computeDeployOnlyInRegion()
  Boolean sendSuccessNotifications = params."Send success notifications"

  if (targetEnvNamespace?.trim()) {
    targetEnv.namespace = targetEnvNamespace
  }

  Deployments deployments = new Deployments()
  node('docker') {
    setBuildDisplayName(chartName, chartVersion, deployOnlyInRegion, targetEnv, deployOnlyInCluster)

    catchError {
      stage("deploy apps") {
        def reportBuildMetadata = { Multimap<ClusterType, AppConfig> apps, Environment env, String buildDesc ->
          currentBuild.description = buildDesc
          // TODO: Has to be reworked in order to uncomment
//          stage("notifications") {
//            sendNotifications(env, chartName, chartVersion, apps, sendSuccessNotifications, deployOnlyInRegion)
//          }
        }
        deployments.deployApps(chartName, chartVersion, targetEnv, deployOnlyInCluster, deployOnlyInRegion, reportBuildMetadata)
      }
    }

    stage('cleanup') {
      cleanWs()
    }
  }
}

void setBuildDisplayName(String chart, String version, Region deployOnlyInRegion, Environment targetEnv, ClusterType deployOnlyInCluster) {
  StringBuilder displayName = new StringBuilder("${currentBuild.number} - ${chart}:${version} -> ${targetEnv.name}")
  if (deployOnlyInRegion) {
    displayName.append(" - only ${deployOnlyInRegion}")
  } else {
    displayName.append(" - all regions")
  }
  if (deployOnlyInCluster) {
    displayName.append(" - only ${deployOnlyInCluster.label}")
  } else {
    displayName.append(" - all clusters")
  }
  currentBuild.displayName = displayName.toString()
}

Region computeDeployOnlyInRegion() {
  Region region = Region.toRegion(params.Region)
  Region deployOnlyInRegion = region
  if (region == Region.ALL) {
    deployOnlyInRegion = null
  }
  deployOnlyInRegion
}

ClusterType computeDeployOnlyInCluster() { ClusterType.toClusterType(params.Cluster) }

Environment computeTargetEnvironment() {
  String environmentInput = params.Environment
  ClusterType clusterType = computeDeployOnlyInCluster()
  Environment targetEnv
  if (clusterType != ClusterType.ALL_IN_CHART) {
    targetEnv = EnvsRegistry.getEnvironment(clusterType, environmentInput)
  } else {
    targetEnv = new Environment(environmentInput, new Cluster(clusterType))
  }
  if (targetEnv == null) {
    throw new IllegalArgumentException("Unknown environment ${environmentInput}. The environment is required.")
  }
  targetEnv
}

void sendNotifications(Environment environment, String chart, String version,
                       Multimap<ClusterType, AppConfig> appsPerCluster, boolean sendSuccessNotifications,
                       Region deployOnlyRegion) {
  Notifications notifications = new Notifications()
  if (currentBuild.resultIsBetterOrEqualTo("SUCCESS")) {
    if (sendSuccessNotifications) {
      notifications.sendSuccessNotification(environment, chart, version, appsPerCluster, deployOnlyRegion)
    }
  } else {
    notifications.sendFailureNotifications()
  }
}
