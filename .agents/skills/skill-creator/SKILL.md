---
name: skill-creator
description: Crée un skill opencode dans .agents/skills/<name>/SKILL.md. Utilise cette skill quand l'utilisateur demande de créer un nouveau skill, d'ajouter un skill, ou de documenter un pattern réutilisable.
---

# Skill Creator

Crée un skill opencode réutilisable dans n'importe quel projet Spring Boot.

## Règles impératives

1. **Ne jamais dépendre du projet existant** — le skill doit être utilisable dans n'importe quel projet
2. **Utiliser des placeholders** comme `{basePackage}` pour les packages, jamais de noms concrets (ex: `com.example.demo`)
3. **DTO auto-suffisant** — si le skill crée un handler d'exception, il doit créer son propre DTO (`ExceptionResponse`) plutôt que de référencer des classes générées du projet
4. **Chemins génériques** — utiliser `src/main/java/{basePackage}/...` au lieu de chemins absolus ou spécifiques au projet
5. **Aucune référence au projet courant** — pas de section "Référence projet" ou de liens vers le projet actuel
6. **Exemples complets** — les exemples de code doivent être copiables directement (avec des placeholders)

## Structure d'un skill

```
.agents/skills/<skill-name>/
  SKILL.md
```

## Format du SKILL.md

```markdown
---
name: <skill-name>
description: <brève description en anglais, utilisée pour le déclenchement automatique>
---

# <Titre du skill>

## Contexte / Problème

Expliquer brièvement ce que le skill résout.

## Implémentation

Donner le code complet avec des placeholders (`{basePackage}`, `{entityName}`, etc.).

## Vérification

Commandes ou étapes pour valider l'implémentation.
```

## Règles de nommage

- Nom du répertoire : `kebab-case` (ex: `custom-exception-handling`)
- `name` dans le frontmatter : `kebab-case`
- `description` : en anglais, commence par un verbe, précise le déclencheur

## Exemple de frontmatter

```yaml
name: custom-exception-handling
description: Implement custom domain exceptions and a global REST exception handler in a Spring Boot application. Use when the user needs to add custom exceptions or standardize API error responses.
```
