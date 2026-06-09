#!/bin/bash
# ============================================================
# Lancement de l'application avec les variables d'environnement
# ============================================================
set -a
source .env
set +a
./gradlew bootRun "$@"
