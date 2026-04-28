#!/bin/bash
set -a
source "$(dirname "$0")/.env"
set +a
~/.sdkman/candidates/maven/current/bin/mvn spring-boot:run -f "$(dirname "$0")/pom.xml"
