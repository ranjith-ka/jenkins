@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def res = new Yaml().load(new FileReader("${WORKSPACE}/jenkins-dsl/yml_config.yml"))

for (repo in res.Application_name)
{
job(repo) {
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
                    execCommand('ansible-playbook ~/non-prod/provider_portal/pp.yml  --extra-vars env_name=${params.envs} task_name=${params.action} ')
                }
            }
        }
    }

    publishers {
        textFinder(/failed=0/, '', true, true, false)
    }
}
}