#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
AGENT="$SCRIPT_DIR/opentelemetry-javaagent.jar"

curl -fL \
  "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.20.1/opentelemetry-javaagent.jar" \
  -o "$AGENT"

echo "OpenTelemetry Java agent downloaded successfully."
