listView('Infra Jobs') {
description('All new jobs for testlist')
filterBuildQueue()
filterExecutors()
jobs {
    name('infra') 
    name('cake') 

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