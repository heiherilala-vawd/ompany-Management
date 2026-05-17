#!/bin/bash

# Chemin du dossier contenant les fichiers
DOSSIER="./src/main/resources/db/testdata"

# Nom du fichier de sortie
FICHIER_SORTIE="../concat.sql"

# On se place dans le dossier
cd "$DOSSIER" || exit

# Vider le fichier de sortie
> "$FICHIER_SORTIE"

# Trier les fichiers par le numéro entre _ et __
for fichier in $(ls *.sql | sort -t'_' -k2,2n); do
    cat "$fichier" >> "$FICHIER_SORTIE"
    echo "" >> "$FICHIER_SORTIE"  # Ajoute une ligne vide entre les fichiers (optionnel)
done

# Message de confirmation
echo "Tous les fichiers ont été fusionnés par ordre numérique dans $FICHIER_SORTIE"