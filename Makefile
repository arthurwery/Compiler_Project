# Nom du fichier zip à créer
ZIP_NAME=project_backup.zip

# Nom du dossier racine dans l'archive zip
ROOT_DIR=your-awesome-compiler-folder

# Cible par défaut
all: zip

# Cible pour créer l'archive zip
zip:
	@echo "Préparation des fichiers..."
	@mkdir -p $(ROOT_DIR)
	@cp -R src $(ROOT_DIR)/src
	@cp -R test $(ROOT_DIR)/test
	@cp build.gradle.kts $(ROOT_DIR)/build.gradle.kts
	@echo "Création de l'archive $(ZIP_NAME)..."
	@zip -r $(ZIP_NAME) $(ROOT_DIR)
	@echo "Archive $(ZIP_NAME) créée avec succès."
	@rm -rf $(ROOT_DIR)

# Cible pour nettoyer les fichiers générés (dans ce cas, le fichier zip et le répertoire temporaire)
clean:
	@echo "Suppression de l'archive $(ZIP_NAME) et du répertoire $(ROOT_DIR)..."
	@rm -f $(ZIP_NAME)
	@rm -rf $(ROOT_DIR)
	@echo "Nettoyage terminé."

.PHONY: all zip clean
