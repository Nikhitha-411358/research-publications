# AI-Driven Renewable-Aware Load Balancing — Enhanced Package

This enhanced project bundle includes:
- forecast/ (FastAPI forecast service + saved model)
- scheduler/ (custom scheduler)
- notebooks/ (forecast_xgb.ipynb, plots.ipynb)
- ml/ (ACO, PSO, training + metrics exposure)
- monitoring/ (Prometheus config, Grafana deployment, node-exporter)
- k8s/ manifests for demo apps and scheduler

## Quick start (kind)
1. Create kind cluster:
   kind create cluster --name renewable-demo

2. Build and load images:
   docker build -t forecast:local -f forecast/Dockerfile forecast/
   docker build -t scheduler:local -f scheduler/Dockerfile.scheduler scheduler/
   docker build -t ml-model:local -f Dockerfile.ml .
   kind load docker-image forecast:local --name renewable-demo
   kind load docker-image scheduler:local --name renewable-demo
   kind load docker-image ml-model:local --name renewable-demo

3. Deploy monitoring:
   kubectl create namespace monitoring
   kubectl apply -f monitoring/prometheus.yaml -n monitoring
   kubectl apply -f monitoring/node-exporter.yaml -n monitoring
   kubectl apply -f monitoring/grafana-deployment.yaml -n monitoring

4. Deploy apps:
   kubectl apply -f k8s/forecast_deploy.yaml
   kubectl apply -f k8s/demo_app.yaml
   kubectl apply -f k8s/scheduler-deployment.yaml

5. Port-forward for local testing:
   kubectl port-forward svc/prom-prometheus-server -n monitoring 9095:80
   kubectl port-forward svc/grafana -n monitoring 3000:3000

6. Train ML model locally (optional):
   cd ml
   docker build -t ml-model:local -f Dockerfile.ml .
   docker run --rm -p 8000:8000 ml-model:local

## Notes
- The Grafana dashboard JSON is available at monitoring/dashboard.json
- Ensure your Prometheus scraping config points to `ml-model-svc.monitoring.svc.cluster.local:8000`
- This package is assembled for demo; you may need to tweak image names and ports per your environment.
