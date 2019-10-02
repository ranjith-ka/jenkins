pipeline {
	options {
	timeout(time: 30, unit: 'MINUTES')
	buildDiscarder(logRotator(numToKeepStr: '5'))
	disableConcurrentBuilds()
	}
environment {
  		  NEXUS_GROUPID='biztalk'
        NEXUS_ENDPOINT = 'http://192.168.100.100:8081/'
		    NEXUS_CRED = credentials('Nexus')
    }
parameters {
      string(
          name: 'package',
          description: 'You want a release version?',
          defaultValue: 'test'
      )
      booleanParam(
          name: 'isrelease',
          description: 'Do you want to release?',
          defaultValue: false
      )
      string(
          name: 'CPU',
          description: 'Where do you want to get notified?',
          defaultValue: ''
      )
    }
	agent {
        node {
            label 'windows'
        }
    }
	stages {

        stage("Initialize variables") {
		    when { expression { BRANCH_NAME ==~ /develop/ } }
            steps {
				      powershell '.\\Scripts\\JenkinsFunctions.ps1 -getCurrentVersion'
            }
        }

		    stage("Run Installer"){
			  when { expression { BRANCH_NAME ==~ /develop/ } }
            steps {
                    powershell '.\\Scripts\\JenkinsFunctions.ps1 -installer'
			      }
			   }

        stage ('Build ESB applications') {
			  when { expression { BRANCH_NAME ==~ /master/ } }
            steps {
                powershell '.\\Scripts\\JenkinsFunctions.ps1 -installDependancy'
			}
		}

		stage('Create deployment package') {
			when { expression { BRANCH_NAME ==~ /master/ } }
			steps {
				powershell '.\\Scripts\\JenkinsFunctions.ps1 -createPackage'
			}
		}

		stage('Create package ZIP') {
			when { expression { BRANCH_NAME ==~ /master/ } }
			steps {
				powershell '.\\Scripts\\JenkinsFunctions.ps1 -createZip'
			}
		}

		stage('Create a tag') {
			when { expression { BRANCH_NAME ==~ /master/ } }
			steps {
				powershell '.\\Scripts\\JenkinsFunctions.ps1 -createTag'
			}
		}
    }

    post {

	   success {
            echo 'Success!'
        }

        failure {
            echo 'Failure!'
        }

        }
}
