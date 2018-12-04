println ""
println "-------Application Builds Creation Process--------"
println ""

File file = new File("${WORKSPACE}/jenkins-dsl/config")
      println "CCX Services file path:${file.absolutePath}"
      println "CCX Config file path:${WORKSPACE}/jenkins-dsl/config"
      println "testing"
        lineNo = 1
        srcProp = [:]
      
      file.withReader { reader ->
         while ((srcProp = reader.readLine()) != null) {
            srcDetails = evaluate("$srcProp")
            println "(${lineNo}): ${srcDetails.appName}: Service Build Job Creation Started-------"
            println ""
           
            def appName = "${srcDetails.appName}"
            println "Application Name: ${appName}"
            
            def projectName = "${srcDetails.projectName}"
            println "ProjectName: ${projectName}"
            
            def repoName = "${srcDetails.repoName}"
            println "RepoNam: ${repoName}"
            
            def view = "${srcDetails.view}"
            println "view: ${view}"
            
            def jFiletId = "${srcDetails.scriptId}".toString()
            println "Jenkins scriptId: ${jFiletId}"
           
                    
            appTeam = "${srcDetails.appTeam}".toString()
            println "AppTeam: ${appTeam}"
            
            // Properties confProp = new Properties()
            //   File confPropertiesFile = new File("${WORKSPACE}/jenkins-dsl/config")
            //   confPropertiesFile.withInputStream {
            //       confProp.load(it)
		    //   } 
            //  def gitCred = evaluate("${confProp.gitCredentials}")
            //  def jHost = "hostname -s".execute().text
            //  def jenHostName = "${jHost}".trim()
           
            //  accPermissions = evaluate("${confProp.accessPermissions}")
            //   accgrp = (accPermissions.get(appTeam))
            //   println "Build Access Granted To: ${accgrp}"
             
            //   emailList = evaluate("${confProp.emailRecipientList}")
            //   grpEmailList = (emailList.get(appTeam)).toString().replaceAll('[\\[\\]]',' ')
            //   println "Email Distribution List: ${grpEmailList}"
             
            //   ccxEnv = evaluate("${confProp.ccxEnv}")
           
           
       
// define the bitbucket project + repos we want to build
// create a pipeline job for each of the repos and for each feature branch.

  multibranchPipelineJob("${appName}-Builds") {
    // configure the branch / PR sources
    displayName ("${appName}")
    description ""
    factory {
        pipelineBranchDefaultsProjectFactory {
            // The ID of the default Jenkinsfile to use from the global Config
            // File Management.
          scriptId "${jFiletId}"

            // If enabled, the configured default Jenkinsfile will be run within
            // a Groovy sandbox.
            useSandbox true
        }
    }
    branchSources {
        git {
          remote("https://bitbucket/scm/${projectName}/${repoName}.git") 
         	credentialsId('dynalean-api-key-credential')
            includes('master release* hotfix*')
        }
    }


    // check every minute for scm changes as well as new / deleted branches
    triggers {
      periodic(120)
    }
    // don't keep build jobs for deleted branches
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(10)
        daysToKeep(20)
      }
    }
    
    // authorization {
    //      accgrp.eachWithIndex { agrp, idx1 ->
    //      permission('hudson.model.Item.Build', "${agrp}")
    //      permission('hudson.model.Item.Cancel', "${agrp}")
    //      permission('hudson.model.Item.Discover', "${agrp}")
    //      permission('hudson.model.Item.Read', "${agrp}")
    //      permission('hudson.model.Item.Workspace', "${agrp}")
    //       }
    //     }
   
   }

println "Repository Url:https://bitbucket/scm/${projectName}/${repoName}.git"
println "${srcDetails.appName}: Service Build Job Created Sucessfully-------"
println ""   
lineNo++
sleep(3000)
//// Add Views 

listView ("${view}") {
description('All new jobs for testlist')
filterBuildQueue()
filterExecutors()
jobs {
    name("${repoName}") 
}
    columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
}
}

    }
        
  }

println ""
println"-----End of Application Builds Creation Process -----"
println ""