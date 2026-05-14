#!/bin/bash

# Chemin du dossier contenant les fichiers
DOSSIER="./src/main/resources/db/testdata"

# Nom du fichier de sortie
FICHIER_SORTIE="../concat.sql"

# On se place dans le dossier
cd "$DOSSIER"

# On concatène tous les fichiers .txt dans le fichier de sortie
cat *.sql > "$FICHIER_SORTIE"

# Message de confirmation
echo "Tous les fichiers ont été fusionnés dans ./src/main/resources/db"
