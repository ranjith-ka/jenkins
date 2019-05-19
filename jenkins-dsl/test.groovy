@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def res = new Yaml().load(new FileReader("${WORKSPACE}/ccx_properties.yml"))


Properties confProp = new Properties()
File confPropertiesFile = new File("$WORKSPACE/ccx_Conf.properties")
confPropertiesFile.withInputStream {
    confProp.load(it)
} 

def jHost = "hostname -s".execute().text
def jenHostName = "${jHost}".trim()

res.appName.each{ project, id ->

def display = (id.commonApp == 'N') ? "${project}-Builds": "${project}-Common"
    
       
// define the bitbucket project + repos we want to build
// create a pipeline job for each of the repos and for each feature branch.

    multibranchPipelineJob(display) {
    // configure the branch / PR sources
    displayName (project)
    description ""
    factory {
        pipelineBranchDefaultsProjectFactory {
            // The ID of the default Jenkinsfile to use from the global Config
            // File Management.
          scriptId id.scriptId

            // If enabled, the configured default Jenkinsfile will be run within
            // a Groovy sandbox.
            useSandbox true
        }
    }
    branchSources {
        git {
          remote("https://bitbucket/scm/${id.projectName}/${id.repoName}.git") 
         	credentialsId(gitCredentials."${jenHostName}")
            includes('master release* hotfix*')
        }
    }


    // check every minute for scm changes as well as new / deleted branches
    triggers {
      periodic(30)
    }
    // don't keep build jobs for deleted branches
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(10)
        daysToKeep(20)
      }
    }
    authorization {
         id.permission.each { agrp -> 
         permission('hudson.model.Item.Build', agrp)
         permission('hudson.model.Item.Cancel', agrp)
         permission('hudson.model.Item.Discover', agrp)
         permission('hudson.model.Item.Read', agrp)
         permission('hudson.model.Item.Workspace', agrp)
          }
        }
   
   }
           
}

res.Application_name.each{ project, id -> 
job(id.name) {
    description()
    keepDependencies(false)
    parameters {
        choiceParam("envs", ["q1", "q2","q3","q4","q5"], "")
        choiceParam("application", ["JBoss"], "")
        choiceParam("action", ["status", "stop", "restart", "startup"], "")
    }
    disabled(false)
    concurrentBuild(false)

    steps {
        publishOverSsh {
            server('Ansible2') {
                transferSet {
                 execCommand("""ansible-playbook ~/non-prod/${id.dd}/\${action}.yml  -e \"env_name=\${envs} task_name=app\"""")       
                  execTimeout(300000)

                }
            }
        }
    }

    publishers {

        textFinder(/failed=0/, '', true, true, false)
    }
}
}

res.deploy.each{ jobs, id ->

def data = id.emailRecipientList as String[]

 job(id.display) {
	description()
	keepDependencies(false)
	parameters {
		stringParam("application_name", jobs, "")
		stringParam("war_version", id.war_version, "Please provide the release no")
		stringParam("env", id.env, "")
		stringParam("repo_name", id.repoName, "")
      	stringParam("artifcat_location","", id.Artifact_location)

	}
	disabled(false)
	concurrentBuild(false)

   configure { project -> 
      def sshNode =  project/builders/"org.jvnet.hudson.plugins.SSHBuilder" 
      sshNode/siteName(id.ansible_node) 
      sshNode/command("""ansible-playbook ~/non-prod/${id.playbook}/\${action}.yml  -e \"env_name=\${envs} task_name=app\"""")
      sshNode/execEachLine (true)
   }  

   publishers {
      textFinder(/failed=0/, '', true, true, false)
      extendedEmail {
        recipientList(data)
        defaultSubject('$PROJECT_DEFAULT_SUBJECT')
        defaultContent('$PROJECT_DEFAULT_CONTENT')
        contentType('default')
        preSendScript ('$DEFAULT_PRESEND_SCRIPT')
        attachBuildLog (false)
        compressBuildLog (false)
        replyToList ('$DEFAULT_REPLYTO')
        saveToWorkspace (false)
        disabled (false)
        triggers {

      failure {
        subject("""deploy_${jobs} build failure""")
        content('$BUILD_URL')
        sendTo {
            recipientList()
            }
        }


      success {
        subject('$PROJECT_DEFAULT_SUBJECT')
        content('$PROJECT_DEFAULT_CONTENT')
        sendTo {
            developers()
            }
          }
        }
    }
}

} 
}

