package com.ft.jenkins.changerequests

import groovy.json.JsonSlurper

import static com.ft.jenkins.changerequests.BizOpsConstants.DEFAULT_CREDENTIALS

final class BizOpsConstants {
  public static final String DEFAULT_CREDENTIALS = "ft.bizops-api.key"
}

String populateSystemCode(String systemCode) {
  switch(systemCode)	{
    case "binary-writer" : return "up-ibw"
    case "body-validation-service" : return "body-validation"
    case "cms-notifier" : return "up-cms-notifier"
    case "kafka-bridges" : return "kafka-bridge"
    case "concept-rw-elasticsearch" : return "up-crwes"
    case "concept-search-api" : return "up-csa"
    case "content-annotator" : return "contentannotator"
    case "content-collection-rw-neo4j" : return "upp-content-collection-rw-neo4j"
    case "content-public-read" : return "contentreadapi"
    case "content-rw-neo4j" : return "upp-content-rw-neo4j"
    case "content-unroller" : return "content-unroller"
    case "curated-authors-memberships-transformer" : return "curated-authors-memberships-tf"
    case "upp-fluentd" : return "content-fluentd"
    case "generic-rw-s3" : return "upp-generic-rw-s3"
    case "ingester" : return "content-ingester"
    case "internal-content-api" : return "up-ica"
    case "kafka" : return "upp-kafka"
    case "list-notifications-rw" : return "upp-list-notifications-rw"
    case "methode-article-internal-components-mapper" : return "up-maicm"
    case "methode-article-mapper" : return "up-mam"
    case "methode-content-collection-mapper" : return "upp-mccm"
    case "methode-content-placeholder-mapper" : return "up-mcpm"
    case "methode-image-binary-mapper" : return "up-mibm"
    case "methode-image-model-mapper" : return "up-mimm"
    case "methode-image-set-mapper" : return "up-mism"
    case "methode-list-mapper" : return "up-mlm"
    case "mongodb" : return "upp-mongodb"
    case "notifications-push" : return "upp-notifications-push"
    case "notifications-push-monitor" : return "upp-notifications-push-monitor"
    case "content-k8s-prometheus" : return "upp-prometheus"
    case "public-annotations-api" : return "annotationsapi"
    case "relations-api" : return "upp-relations-api"
    case "smartlogic-concordance-transformer" : return "smartlogic-concordance-transform"
    case "synthetic-image-publication-monitor" : return "synth-image-pub-monitor"
    case "system-healthcheck" : return "upp-system-healthcheck"
    case "upp-next-video-annotations-mapper" : return "up-nvam"
    case "upp-next-video-content-collection-mapper" : return "upp-next-video-cc-mapper"
    case "upp-next-video-mapper" : return "next-video-mapper"
    case "upp-schema-reader" : return "json-schema-reader"
    case "wordpress-article-mapper" : return "up-wam"
    case "upp-content-validator" : return "pac-upp-content-validator"
    case "content-feed-admin": return "cm-content-feed-admin"
    default: return systemCode
	}
}

String checkSystemCode(String systemCode, String credentialId = DEFAULT_CREDENTIALS)   {
    echo "Checking systemCode in Biz-Ops"
    try {
        String bizOpsURL = 'https://api.ft.com/biz-ops/graphql?query='
        String query = '{System(code:"' + systemCode + '"){code}}'
        String encodedQuery = URLEncoder.encode(query)
        String encodedURL = bizOpsURL + encodedQuery
        def response
        withCredentials([string(credentialsId: credentialId, variable: 'UPP_BIZOPS_API_KEY')]) {
            response = httpRequest(httpMode: 'GET',
                            url: "${encodedURL}",
                            customHeaders: [[maskValue: true, name: 'x-api-key', value: env.UPP_BIZOPS_API_KEY],
                                            [maskValue: false, name: 'content-type', value: 'application/json'],
                                            [maskValue: false, name: 'client-id', value: 'upp-jenkins']],
                            timeout: 60,
                            consoleLogResponseBody: true)
        }
        def responseJson = new JsonSlurper().parseText(response.content)

        if (responseJson.data.System.code == systemCode)    {
            echo "System code ${systemCode} found in Biz-Ops"
            return systemCode
        }   else    {
            echo "System code ${systemCode} not found in Biz-Ops, setting it to upp"
            return "upp"
        }
    }
    catch (e)   {
        echo "Error while checking for systemCode in Biz-Ops: ${e.message} "
        return "upp"
    }
}
