// define the bitbucket project + repos we want to build
def bitbucket_project = 'test'
def bitbucket_repos = ['test1, test2']

// create a pipeline job for each of the repos and for each feature branch.
for (bitbucket_repo in bitbucket_repos)
{
  multibranchPipelineJob("${bitbucket_repo}") {

    // configure the branch / PR sources
    displayName ("${bitbucket_repo}")
    description ""


    factory {
        pipelineBranchDefaultsProjectFactory {
            // The ID of the default Jenkinsfile to use from the global Config
            // File Management.
            scriptId 'Jenkinsfile'

            // If enabled, the configured default Jenkinsfile will be run within
            // a Groovy sandbox.
            useSandbox true
        }
    }
    branchSources {
      branchSource {
        source {
          github {
            credentialsId('api-credential')
            remote("https://bitbucket.org/")
            repoOwner("${bitbucket_project}")
            repository("${bitbucket_repo}")
          }
        }
        strategy {
          defaultBranchPropertyStrategy {
            props {
              // keep only the last 10 builds
              buildRetentionBranchProperty {
                buildDiscarder {
                  logRotator {
                    daysToKeepStr("-1")
                    numToKeepStr("10")
                    artifactDaysToKeepStr("-1")
                    artifactNumToKeepStr("-1")
                  }
                }
              }
            }
          }
        }
      }
    }


    // discover Branches (workaround due to JENKINS-46202)
    configure {
      def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
      traits << 'com.cloudbees.jenkins.plugins.bitbucket.BranchDiscoveryTrait' {
        strategyId(3) // detect all branches
      }
    }
        configure { node ->
        // node represents <project>
        jdk('Java 10')
    }

    // check every minute for scm changes as well as new / deleted branches
    triggers {
      periodic(120)
    }
    // don't keep build jobs for deleted branches
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(20)
      }
    }
  }
}
