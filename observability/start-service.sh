#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVICE="${1:-}"

case "$SERVICE" in
  order-service|payment-service|inventory-service)
    ;;
  *)
    echo "Usage: $0 {order-service|payment-service|inventory-service}"
    exit 1
    ;;
esac

AGENT="$ROOT_DIR/observability/opentelemetry-javaagent.jar"

if [ ! -f "$AGENT" ]; then
  echo "OpenTelemetry agent not found: $AGENT"
  exit 1
fi

export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

export JAVA_TOOL_OPTIONS="-javaagent:$AGENT"
export OTEL_SERVICE_NAME="$SERVICE"
export OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318"
export OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"
export OTEL_TRACES_EXPORTER="otlp"
export OTEL_METRICS_EXPORTER="none"
export OTEL_LOGS_EXPORTER="none"

echo "Starting $SERVICE with OpenTelemetry..."

cd "$ROOT_DIR/demo-services/$SERVICE"
exec mvn spring-boot:run
