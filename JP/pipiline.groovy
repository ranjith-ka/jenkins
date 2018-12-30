def DeployMicroServices(nodeListKey) {
    echo "Value passed is $nodeListKey"
    return {
       stage("Deploying $nodeListKey Services") {
            env.nodeListKey="$nodeListKey"
            echo "Value passed inside stage is $nodeListKey"
            sh''' 
                echo "Value passed inside shell is $nodeListKey"
                echo "Recipe list is $nodeListKey_ipAddresses"
                ipaddresses=$(echo ${nodeListKey}_ipAddresses)
                consulRecipeList=$(echo ${nodeListKey}_runListDeployApp)
                echo ${!ipaddresses}
                echo ${!consulRecipeList}
                for node in ${!ipaddresses}
                do
                      echo "Connecting to $node"
                      if [[ ! -z ${!consulRecipeList} ]]
                      then
                      ssh -q -i  $COMMON_SSH_KEY $COMMON_SSH_USER@$node "echo `hostname`"
                      ssh -q -i  $COMMON_SSH_KEY $COMMON_SSH_USER@$node "echo Recipe list is ${!consulRecipeList}"
                      ssh -q -i  $COMMON_SSH_KEY $COMMON_SSH_USER@$node "sudo chef-client -o ${!consulRecipeList}"
                      ssh -q -i  $COMMON_SSH_KEY $COMMON_SSH_USER@$node 'ps -ef | grep tomcat_ | grep -v grep | awk "{print \\$2}"'
                      fi                     
                done
             '''
        }
    }
}

pipeline {

    agent { label 'jenkins-slave' }
    
    stages {
        stage('setEnvironment'){
            steps{
        configFileProvider([configFile(fileId: '764358a6-e640-4a58-a042-dd760b8a0a21', targetLocation: 'FORD_pod1_node_list1', variable: 'varProdFORDNodeList')]) {
        script {
                        def props = readJSON file: varProdFORDNodeList
                        props["nodes"].each {
                            def nodetype = it.getKey()
                            props["nodes"][nodetype].each { dict ->
                                def key=dict.getKey()
                                def value=dict.getValue()
                                env."${nodetype}_${key}" = value
                            }
                        }
                    }
        }
            }
        }
        stage('Deploy CMS Batch1 Services'){
            steps{
                script {
                    def jobMap = [
                        'DeployFORD_MAIDS': DeployMicroServices('MAIDS'),
                        'DeployFORD_PKGR': DeployMicroServices('FORD_PKGR'),
                        'DeployFORD_JLSDI': DeployMicroServices('FORD_JLSDI'),
                        'DeployFORD_FRS': DeployMicroServices('FORD_FRS'),
                        'DeployFORD_LIC': DeployMicroServices('FORD_LIC'),
                        'DeployFORD_MIG': DeployMicroServices('FORD_MIG'),
                        'DeployFORD_CMS2': DeployMicroServices('FORD_CMS2'),
                        'DeployFORD_SS': DeployMicroServices('FORD_SS'),
                        'DeployFORD_SCH': DeployMicroServices('FORD_SCH')
                    ]
                    parallel jobMap
                }
            }
        }
  }
}