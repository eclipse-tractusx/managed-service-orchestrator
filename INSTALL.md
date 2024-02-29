## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/managed-service-orchestrator

 - Using helm commands <br />

How to install application using helm:  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository in tractusx:
           helm repo add tractusx https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo tractusx/managed-service-orchestrator
    c.) To install using helm command:   
           helm install orchestrator tractusx/managed-service-orchestrator


Local installation:

1. Install Kubeapps on your local Kubernetes cluster. 
   Installation for kubeapps can be taken from reference https://docs.bitnami.com/tutorials/install-use-kubeapps/

2. Add Package repository in Kubeapps https://eclipse-tractusx.github.io/charts/dev/

3. Create namespace through Kubeapps

4. Select the Package Repository which was added in step 2

5. Select managed-service-orchestrator from the Catalog

6. To set your own configuration and secret values in values file

7. Deploy the application

   