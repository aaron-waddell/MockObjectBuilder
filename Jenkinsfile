// Groovy Script File for Jenkins
// Name: Jenkinsfile
// Created: 03/13/2020
// Author(s): George Daoud
// Purpose: The intended purpose of this script is to possibly perform a build
// followed by a deployment operation within the appropriate cluster(s). 

node {
    //def mvnHome // Is this needed?
    def clusterId = "311-np-1"
    def appName = "edi-order-services"

   properties([
        parameters([
			choiceParam(name: 'projectName', choices: ['cust-svc-np','gtm-integration-np','order-mgt-np'], description: 'Openshift project'),
			choiceParam(name: 'ocpCredId', choices: ['jenkins-cust-svc-np-311','jenkins-gtm-integration-np-311','jenkins-order-mgt-np-op311'], description: 'Openshift project Credential Id'),
            choiceParam(name: 'branch', choices:['master', 'dev']), 
            choiceParam(name: 'environment', choices:['dev', 'sys', 'qa'])]), 			
     buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),
     [$class: 'ThrottleJobProperty', categories: [], limitOneJobWithMatchingParams: false, maxConcurrentPerNode: 0, maxConcurrentTotal: 1, paramsToUseForLimit: '', throttleEnabled: true, throttleOption: 'project'],
     pipelineTriggers([])])
   stage('Preparation') { // for display purposes
      branchLabel = params.branch
      
      println "Branch Label: ${branch}"
     //from snippet generator for 'checkout'

	checkout([$class: 'GitSCM', 
          branches: [[name: "refs/heads/${branchLabel}"]], 
          browser: [$class: 'GogsGit', repoUrl: ''], 
          doGenerateSubmoduleConfigurations: false, 
          extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], 
          userRemoteConfigs: [[credentialsId: 'jenkins-git', 
            url: 'https://git.shawinc.com/csds-src/edi-order-services']]]          
      )



      // Get the tools.
      // ** NOTE: These tools must be configured
      // **       in the global configuration.           
      mvnHome  = tool 'Maven 3.6.1'
      javaHome = tool 'JDK 8'
      oc = tool 'OC 3.11'
   }
   stage('Build') {
      // Run the maven build
      sh "JAVA_HOME=${javaHome} '${mvnHome}/bin/mvn' clean package -Dmaven.test.skip=true"
   }
   stage('Archiving') {
      archiveArtifacts  'target/*.jar'
   }
   withEnv(["PATH+OC=${oc}"]) {

      stage ('Image Build via OSCP'){         
          openshift.withCluster(clusterId) {
             openshift.withProject(params.projectName) {
                openshift.withCredentials(params.ocpCredId){
                   bc = openshift.selector("bc/${appName}")
                   if (bc.exists()){
                     b = bc.startBuild('--from-file=target/edi-order-services.jar')
                     b.logs("--follow=true")
                   } else {
                     error('Build configuration not found!')
                   } 
                }
             }
          }
      }

      stage ('Rollout Test Env'){         
          openshift.withCluster(clusterId) {
             openshift.withProject(params.projectName) {
                openshift.withCredentials(params.ocpCredId){
                  dc = openshift.selector("dc/${appName}-${environment}")
                  if (dc.exists()) {
                     d = dc.rollout().latest()
                  } else {
                     println 'Deployment config not found'
                  }
               }
            }
         }
      }
   }
}