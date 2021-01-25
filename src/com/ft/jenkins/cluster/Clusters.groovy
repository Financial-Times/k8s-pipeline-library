package com.ft.jenkins.cluster

import com.cloudbees.groovy.cps.NonCPS

import static EnvClusterMapEntry.newEksEntry
import static EnvClusterMapEntry.newEntry
import static com.ft.jenkins.cluster.Environment.*

class Clusters implements Serializable {

  private static final String SLACK_CHANNEL = "#upp-changes"

  @NonCPS
  static Cluster initDeliveryCluster() {
    Cluster deliveryCluster = new Cluster(ClusterType.DELIVERY)
    Environment devEnv = new Environment(DEV_NAME, deliveryCluster)
    devEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU]
      associatedClusterTypes = [ClusterType.DELIVERY, ClusterType.PUBLISHING]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-dev-eu",
                      apiServer: "https://47395EC9585B62EF624684D7D4F53178.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-k8s-dev-delivery-eu.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-k8s-dev-publish-eu.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-k8s-dev-delivery-eu.upp.ft.com"
      ]
    }

    Environment k8sEnv = new Environment("k8s", deliveryCluster)
    k8sEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU]
      associatedClusterTypes = [ClusterType.DELIVERY, ClusterType.PUBLISHING]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-dev-eu",
                      apiServer: "https://47395EC9585B62EF624684D7D4F53178.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-k8s-dev-delivery-eu.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-k8s-dev-publish-eu.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-k8s-dev-delivery-eu.upp.ft.com"
      ]
    }

    Environment stagingEnv = new Environment(STAGING_NAME, deliveryCluster)
    stagingEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.DELIVERY, ClusterType.PUBLISHING]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-staging-eu",
                      apiServer: "https://078C9263D342171D82322E6F310E7CB5.yl4.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-staging-delivery-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-staging-us",
                      apiServer: "https://3EB5C85F5B2D7B15E830EA2DADBCAE10.gr7.us-east-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-staging-delivery-us.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-staging-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-staging-delivery-glb.upp.ft.com"
      ]
    }

    Environment prodEnv = new Environment(PROD_NAME, deliveryCluster)
    prodEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.DELIVERY, ClusterType.PUBLISHING]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-prod-eu",
                      apiServer: "https://0B6F827AA113F0CD0DD190732C31C0B5.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-prod-delivery-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.DELIVERY}".toString()): newEksEntry(
                      eksClusterName: "eks-delivery-prod-us",
                      apiServer: "https://EC32E3A1583C787C268DECEFA781C870.gr7.us-east-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-prod-delivery-us.upp.ft.com",
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-prod-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-prod-delivery-glb.upp.ft.com"
      ]
    }

    deliveryCluster.environments = [devEnv, k8sEnv, stagingEnv, prodEnv]
    deliveryCluster
  }

  @NonCPS
  static Cluster initPublishingCluster() {
    Cluster publishingCluster = new Cluster(ClusterType.PUBLISHING)
    Environment devEnv = new Environment(DEV_NAME, publishingCluster)
    devEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU]
      associatedClusterTypes = [ClusterType.PUBLISHING, ClusterType.DELIVERY]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-dev-eu",
                      apiServer: "https://25C9DAC5C2EA30A99DE9DBE542EF6EBC.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-k8s-dev-publish-eu.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-k8s-dev-publish-eu.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-k8s-dev-delivery-eu.upp.ft.com"
      ]
    }

    Environment k8sEnv = new Environment("k8s", publishingCluster)
    k8sEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU]
      associatedClusterTypes = [ClusterType.PUBLISHING, ClusterType.DELIVERY]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-dev-eu",
                      apiServer: "https://25C9DAC5C2EA30A99DE9DBE542EF6EBC.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-k8s-dev-publish-eu.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-k8s-dev-publish-eu.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-k8s-dev-delivery-eu.upp.ft.com"
      ]
    }

    Environment stagingEnv = new Environment(STAGING_NAME, publishingCluster)
    stagingEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.PUBLISHING, ClusterType.DELIVERY]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-staging-eu",
                      apiServer: "https://3E7DE7DAA0CC7D57BE82E8C88FD94615.gr7.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-staging-publish-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-staging-us",
                      apiServer: "https://D5603899E9C084F9A1EF82AEF4A84CFA.gr7.us-east-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-staging-publish-us.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-staging-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-staging-delivery-glb.upp.ft.com"
      ]
    }

    Environment prodEnv = new Environment(PROD_NAME, publishingCluster)
    prodEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.PUBLISHING, ClusterType.DELIVERY]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-prod-eu",
                      apiServer: "https://156143A5210911D60E8E6EE9AEDADDD1.sk1.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-prod-publish-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.PUBLISHING}".toString()): newEksEntry(
                      eksClusterName: "eks-publish-prod-us",
                      apiServer: "https://3AC0172E91A277DED0F8CF21C20640AB.gr7.us-east-1.eks.amazonaws.com",
                      publicEndpoint: "https://upp-prod-publish-us.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-prod-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-prod-delivery-glb.upp.ft.com"
      ]
    }
    publishingCluster.environments = [devEnv, k8sEnv, stagingEnv, prodEnv]
    publishingCluster
  }

  @NonCPS
  static Cluster initPacCluster() {
    Cluster pacCluster = new Cluster(ClusterType.PAC)
    Environment stagingEnv = new Environment(STAGING_NAME, pacCluster)
    stagingEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.PAC]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PAC}".toString()): newEksEntry(
                      eksClusterName: "eks-pac-staging-eu",
                      apiServer: "https://22A1CF6542D92E344067937BAC79DBE7.gr7.eu-west-1.eks.amazonaws.com/",
                      publicEndpoint: "https://pac-staging-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.PAC}".toString()): newEksEntry(
                      eksClusterName: "eks-pac-staging-us",
                      apiServer: "https://A1F34514BBCD077F05507FBA51968D5C.gr7.us-east-1.eks.amazonaws.com/",
                      publicEndpoint: "https://pac-staging-us.upp.ft.com"
              )
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-staging-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-staging-delivery-glb.upp.ft.com"
      ]
    }

    Environment prodEnv = new Environment(PROD_NAME, pacCluster)
    prodEnv.with {
      slackChannel = SLACK_CHANNEL
      regions = [Region.EU, Region.US]
      associatedClusterTypes = [ClusterType.PAC]
      clusterToApiServerMap = [
              ("${Region.EU}-${ClusterType.PAC}".toString()): newEksEntry(
                      eksClusterName: "eks-pac-prod-eu",
                      apiServer: "https://3C344FA4ADC11DE5838327DF594DC025.yl4.eu-west-1.eks.amazonaws.com",
                      publicEndpoint: "https://pac-prod-eu.upp.ft.com"
              ),
              ("${Region.US}-${ClusterType.PAC}".toString()): newEksEntry(
                      eksClusterName: "eks-pac-prod-us",
                      apiServer: "https://F88282076FE9CDAD097E27E68CCE4D18.gr7.us-east-1.eks.amazonaws.com",
                      publicEndpoint: "https://pac-prod-us.upp.ft.com"
              ),
      ]
      glbMap = [
              (ClusterType.PUBLISHING.toString()): "https://upp-prod-publish-glb.upp.ft.com",
              (ClusterType.DELIVERY.toString())  : "https://upp-prod-delivery-glb.upp.ft.com"
      ]
    }
    pacCluster.environments = [stagingEnv, prodEnv]
    pacCluster
  }
}
