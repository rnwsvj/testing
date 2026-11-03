#!/bin/sh
# Gradle wrapper launcher
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
else
    echo "Using default system gradle to assemble..."
    gradle "$@"
fi
