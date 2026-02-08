#!/bin/bash
# wait-for-services.sh

set -e

host="$1"
shift
cmd="$@"

until curl -f http://$host/actuator/health; do
  >&2 echo "Service is unavailable - sleeping"
  sleep 10
done

>&2 echo "Service is up - executing command"
exec $cmd