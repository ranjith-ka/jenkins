job("myansible-job") {
	description()
	keepDependencies(false)
	parameters {
		stringParam("application_name", "provider_portal", "")
		stringParam("war_version", "1.27.20", "Please provide the release no")
		stringParam("env", "q1", "")
		stringParam("repo_name", "portal-app-group", "")
	}
	disabled(false)
	concurrentBuild(false)

    steps {
        publishOverSsh {
            server('Ansible2') {
                transferSet {
                    execCommand('ansible-playbook ~/non-prod/provider_portal/pp.yml  --extra-vars "env_name=${env} repo_name=${repo_name} release_version=${war_version}" ')
                }
            }
        }
    }

    publishers {
        textFinder(/failed=0/, '', true, true, false)
    }
}