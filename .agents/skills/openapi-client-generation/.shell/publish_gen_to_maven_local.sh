cd build/gen && mvn clean install
set -e

SRC="./src/main/java/{basePackage}/client"
DEST="../../src/main/java/{basePackage}/client"

echo "📦 Suppression de l'ancien dossier..."
rm -rf "$DEST"

echo "📁 Copie du nouveau dossier..."
mkdir -p "$(dirname "$DEST")"
cp -r "$SRC" "$DEST"

echo "✅ Copie terminée avec succès."