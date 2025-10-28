# Student Portal Monorepo

Un portail étudiant moderne centralisant les services administratifs, académiques et financiers d'une école. Le dépôt contient :

- **frontend/** – Application Next.js 14 (App Router) avec TypeScript, Tailwind CSS, React Query et Zustand.
- **backend/** – API Spring Boot 3 protégeant les données (JWT) et orchestrant les processus étudiants.
- **payment-simulator/** – Microservice Spring Boot simulant un prestataire de paiement.
- **infra/** – Docker Compose pour Postgres, MailHog et PgAdmin.
- **.vscode/** – Configuration prête à lancer dans VS Code (debug & tâches).

## Sommaire

1. [Prérequis](#prérequis)
2. [Installation rapide](#installation-rapide)
3. [Variables d'environnement](#variables-denvironnement)
4. [Données de démonstration](#données-de-démonstration)
5. [Services](#services)
6. [Tests](#tests)
7. [Workflow de développement](#workflow-de-développement)
8. [Swagger & OpenAPI](#swagger--openapi)
9. [Scénarios à tester](#scénarios-à-tester)
10. [Captures d'écran](#captures-décran)

## Prérequis

- Node.js 20+
- Java 23 + Maven Wrapper
- Docker & Docker Compose
- VS Code (recommandé) avec les extensions suggérées dans `.vscode/extensions.json`

## Installation rapide

```bash
cd student-portal
# Lancer l'infrastructure de base de données & mail
cd infra
cp .env.example .env
docker compose up -d

# Lancer le backend principal
cd ../backend
./mvnw spring-boot:run

# Lancer le simulateur de paiement
cd ../payment-simulator
./mvnw spring-boot:run

# Lancer le frontend Next.js
cd ../frontend
npm install
npm run dev
```

Accédez ensuite au portail sur http://localhost:3000.

## Variables d'environnement

### Frontend

Copiez `.env.example` vers `.env.local` et adaptez si besoin :

```
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=changeme-in-dev
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws/notifications
```

### Backend principal

Le backend charge ses propriétés depuis `src/main/resources/application.yml`. Un profil `dev` est prévu pour les environnements locaux et s'appuie sur Docker Compose.

### Service simulateur de paiement

Configuration via `payment-simulator/src/main/resources/application.yml`. Ajustez la variable `gateway.webhook-url` pour pointer vers votre backend si l'URL diffère.

## Données de démonstration

Les migrations Flyway créent un jeu de données de démonstration :

- **Utilisateurs**
  - Étudiant : `student1@school.test` / `Passw0rd!`
  - Étudiant : `student2@school.test` / `Passw0rd!`
  - Professeur : `professor@school.test` / `Passw0rd!`
    - modules enseign�s : Network Forensics & Data Visualization Studio (gestion des notes et absences)
  - Admin : `admin@school.test` / `Passw0rd!`
- **Notes** : 4 modules récents
- **Emploi du temps** : semaine en cours avec cours et examens
- **Demandes** : 2 demandes avec statuts variés et pièces justificatives
- **FAQ** : 3 questions fréquentes
- **Paiements** : 1 réussi, 1 échoué (webhook simulé)

## Services

| Service | Port | Description |
|---------|------|-------------|
| Frontend Next.js | 3000 | Portail web |
| Backend API | 8080 | REST + WebSocket + Swagger |
| Simulateur paiement | 9090 | API factice + Swagger |
| Postgres | 5432 | Base de données principale |
| Mailhog | 8025 | Interface de messagerie simulée |
| PgAdmin | 5050 | Administration Postgres |

## Tests

```bash
# Frontend
cd student-portal/frontend
npm run test

# Backend principal
cd ../backend
./mvnw test

# Simulateur de paiement
cd ../payment-simulator
./mvnw test
```

Les tests backend utilisent Testcontainers pour lancer PostgreSQL et vérifier les services critiques.

## Workflow de développement

1. Démarrer l'infrastructure Docker.
2. Lancer le backend en mode dev (`./mvnw spring-boot:run`).
3. Lancer le simulateur de paiement (même commande) pour disposer d'une API de paiement factice.
4. Démarrer le frontend (`npm run dev`).
5. Utiliser VS Code :
   - `Run > Start Debugging` pour lancer la configuration **Backend - Spring Boot** ou **Frontend - Next.js**.
   - `Terminal > Run Task` pour exécuter les tâches prédéfinies (`npm install`, `mvnw clean verify`, `docker compose up`).

## Swagger & OpenAPI

- Backend principal : http://localhost:8080/swagger-ui/index.html (description YAML disponible sous `/v3/api-docs.yaml`).
- Simulateur de paiement : http://localhost:9090/swagger-ui/index.html (`/v3/api-docs.yaml`).

## Scénarios à tester

1. **Authentification** : connexion, actualisation du token, accès aux pages protégées.
2. **Demandes administratives** : création d'une demande, changement de statut par le staff, suivi étudiant, notifications temps réel.
3. **Paiements** : création d'intention, confirmation via simulateur, réception webhook, mise à jour du statut.
4. **Messagerie** : création d'un fil, envoi de messages par le staff, réception en temps réel côté étudiant.
5. **Chatbot FAQ** : poser une question fréquente, vérifier le matching, parcours fallback.
6. **Emploi du temps** : navigation semaine, export ICS.
7. **Notes** : consultation, tri, recherche.

## Captures d'écran

Les captures d'écran de référence sont disponibles dans `frontend/public/screenshots/`.


