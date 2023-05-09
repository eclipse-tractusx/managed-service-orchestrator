## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/autosetup-backend

 - Using helm commands <br />

How to install application using helm:  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository in tractusx:
           helm repo add autosetup https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo autosetup/orchestrator
    c.) To install using helm command:   
           helm install autosetup autosetup/orchestrator

