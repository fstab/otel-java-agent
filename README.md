# Getting Started with the OpenTelemetry Java Agent

## Example Project

Build the example application

```
git clone https://github.com/spring-projects/spring-petclinic.git
cd spring-petclinic
./mvnw package
cd ..
```

Download the latest `otelcol` release from https://github.com/open-telemetry/opentelemetry-collector-releases/releases.

```
mkdir otelcol
curl -OL https://github.com/open-telemetry/opentelemetry-collector-releases/releases/download/v0.44.0/otelcol_0.44.0_linux_amd64.tar.gz
tar xfz otelcol_0.44.0_linux_amd64.tar.gz -C ./otelcol/
```

This should create a `./otelcol/otelcol` executable.

## Collector

We want to be able to configure different log levels for traces, metrics, and logs. Therefore, instead of
just defining a `logging` exporter, we define three: `loggging/metrics`, `logging/traces`, `logging/logs`.

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:

processors:
  batch:

exporters:
   logging/metrics:
     logLevel: debug
   logging/traces:
     logLevel: warn
   prometheusremotewrite:
     endpoint: "http://localhost:9090/api/v1/write"

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging/traces]
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [logging/metrics,prometheusremotewrite]
```

## Metrics

Exporting metrics is disabled by default. Enable it with:

```
export OTEL_METRICS_EXPORTER=otlp
```

Now start the app with the agent attached

```
curl -LO https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.11.0/opentelemetry-javaagent.jar
java -javaagent:opentelemetry-javaagent.jar -jar ./target/spring-petclinic-2.6.0-SNAPSHOT.jar r -jar ./target/spring-petclinic-2.6.0-SNAPSHOT.jar
```

Prometheus Server

```
curl -OL https://github.com/prometheus/prometheus/releases/download/v2.33.3/prometheus-2.33.3.linux-amd64.tar.gz
tar xfz prometheus-2.33.3.linux-amd64.tar.gz
```

Run the Prometheus server with the remote write receiver endpoint enabled

```
cd ./prometheus-2.33.3.linux-amd64/
./prometheus --web.enable-remote-write-receiver
```

Run `otelcol`

```
./otelcol/otelcol --config=./otelcol-config.yaml
```