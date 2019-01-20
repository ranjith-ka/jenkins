#!groovy

pipeline {
    agent { node { label 'master' } }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }
  
	environment {
			NEXUS_CRED = credentials('Nexus')
			GRADLE_NEXUS_CREDS = "-q -PnexusUser=${env.NEXUS_CRED_USR} -PnexusPassword=${env.NEXUS_CRED_PSW}"
		}
	parameters {
		booleanParam(
			name: 'isRelease',
			description: 'Check its a release branch',
			defaultValue: false
		)
	}
	stages {
		//-----------------------------------
		// Checkout SCM
		//-----------------------------------
		stage('Checkout SCM'){
			steps{ 
			sh 'git config --global http.sslverify false'
			checkout scm
			echo 'COMPLETE: Checkout SCM'
			}
		}
		stage('Testing') {
			steps {
               echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
			        script {
						currentBuild.displayName = "build"+ "${env.BUILD_ID}"
						def now = new Date()
        				def test =  now.format("yyMM", TimeZone.getTimeZone('UTC'))
						echo test
				}
            }
    	}		
	}
}