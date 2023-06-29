## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/managed-service-orchestrator

 - Using helm commands <br />

How to install application using helm:  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository in tractusx:
           helm repo add orchestrator https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo orchestrator/autosetup
    c.) To install using helm command:   
           helm install orchestrator orchestrator/autosetup
