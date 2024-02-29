## Installation Steps

A helm chart is provided inside the [charts](charts/orchestrator) directory

How to install application using helm:  <br />

              helm install [ReleaseName] [ChartName]
    
1.) Installation from released chart: <br />
    
    a.) Add helm repository in tractusx:
           helm repo add tractusx-dev https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo tractusx-dev/managed-service-orchestrator
    c.) To install using helm command:   
           helm install orchestrator tractusx-dev/managed-service-orchestrator

2.) Local installation:
    
    a. Install Kubeapps on your local Kubernetes cluster. 
       Installation for kubeapps can be taken from reference https://docs.bitnami.com/tutorials/install-use-kubeapps

    b. Add Package repository in Kubeapps https://eclipse-tractusx.github.io/charts/dev/

    c. Create namespace through Kubeapps

    d. Select the Package Repository which was added in step 2

    e. Select managed-service-orchestrator from the Catalog

    f. To set your own configuration and secret values in values file

    g. Deploy the application
