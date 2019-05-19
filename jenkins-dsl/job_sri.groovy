@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def res = new Yaml().load(new FileReader("${WORKSPACE}/jenkins-dsl/yml_config.yml"))

res.Application_name.each{ project, id -> 
def display = (appsName == 'id.commonApp' ) ? "${project}-Builds": "${project}-Common"
job(project) {
    description()
    keepDependencies(false)
    parameters {
        choiceParam("envs", ["q1", "q2"], "")
        choiceParam("application", ["JBoss"], "")
        choiceParam("action", ["status", "stop", "restart", "startup"], "")
    }
    disabled(false)
    concurrentBuild(false)

    steps {
        publishOverSsh {
            server('Ansible2') {
                transferSet {
                    execCommand("""ansible-playbook ~/non-prod/${id.dd}/tes.yml  --extra-vars env_name=\${envs} task_name=\${action} """)
                }
            }
        }
    }

    publishers {
        textFinder(/failed=0/, '', true, true, false)
    }
}
}
