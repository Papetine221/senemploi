# Contrôle d'accès par rôle - Senemploi

## 🎯 Rôles disponibles

1. **ROLE_CANDIDAT** - Candidats cherchant un emploi
2. **ROLE_RECRUTEUR** - Recruteurs/Entreprises publiant des offres
3. **ROLE_ADMIN** - Administrateurs de la plateforme

---

## 📋 Matrice d'accès - Navigation

### Pour tous (non authentifiés)
- ✅ Page d'accueil `/`
- ✅ Page de connexion `/login`
- ✅ Page d'inscription `/account/register`
- ✅ Réinitialisation mot de passe `/account/reset`

### ROLE_CANDIDAT uniquement
- ✅ **Tableau de bord candidat** `/candidat-dashboard`
- ✅ **Mes candidatures** `/candidature/mes-candidatures`
- ✅ Voir les offres d'emploi `/offre-emploi`
- ✅ Postuler aux offres
- ✅ Gérer son profil candidat
- ✅ Paramètres de compte `/account/settings`

### ROLE_RECRUTEUR uniquement
- ✅ **Tableau de bord recruteur** `/recruteur-dashboard`
- ✅ Créer/modifier/supprimer des offres d'emploi
- ✅ Voir les candidatures reçues
- ✅ Gérer son profil entreprise
- ✅ Voir les offres d'emploi `/offre-emploi`
- ✅ Paramètres de compte `/account/settings`

### ROLE_ADMIN uniquement
- ✅ **Menu Administration** `/admin`
  - Gestion des utilisateurs
  - Métriques système
  - Diagnostics
  - Configuration
  - Logs
  - API Docs (si OpenAPI activé)
- ✅ **Menu Entités** (gestion CRUD complète)
  - Candidats `/candidat`
  - Recruteurs `/recruteur`
  - Offres d'emploi `/offre-emploi`
  - Candidatures `/candidature`
  - Types de contrat `/type-contrat`
  - Localisations `/localisation`
  - Compétences `/competence`
  - Authorities `/authority`
- ✅ Accès à tout le reste de la plateforme

### Pour tous les utilisateurs authentifiés
- ✅ **Offres d'emploi** `/offre-emploi` (consultation)
- ✅ Profil utilisateur `/account/settings`
- ✅ Changement de mot de passe `/account/password`
- ✅ Déconnexion

---

## 🔒 Implémentation technique

### Frontend (Angular)

#### Directive `*jhiHasAnyAuthority`
Utilisée dans les templates pour afficher conditionnellement des éléments selon le rôle :

```html
<!-- Exemple: Visible uniquement pour CANDIDAT -->
<li *jhiHasAnyAuthority="'ROLE_CANDIDAT'">
  <a routerLink="/candidat-dashboard">Tableau de bord</a>
</li>

<!-- Exemple: Visible uniquement pour RECRUTEUR -->
<li *jhiHasAnyAuthority="'ROLE_RECRUTEUR'">
  <a routerLink="/recruteur-dashboard">Tableau de bord recruteur</a>
</li>

<!-- Exemple: Visible uniquement pour ADMIN -->
<li *jhiHasAnyAuthority="'ROLE_ADMIN'">
  <a routerLink="/admin">Administration</a>
</li>
```

#### Guard `UserRouteAccessService`
Protection des routes dans `app.routes.ts` :

```typescript
{
  path: 'candidat-dashboard',
  loadComponent: () => import('./candidat-dashboard/candidat-dashboard.component'),
  canActivate: [UserRouteAccessService],
  data: {
    authorities: ['ROLE_CANDIDAT'],
  },
}
```

### Backend (Java Spring)

#### Annotations de sécurité
```java
@PreAuthorize("hasAuthority('ROLE_CANDIDAT')")
public ResponseEntity<CandidatureDTO> postuler(@RequestBody CandidatureDTO candidatureDTO) {
    // Logique métier
}

@PreAuthorize("hasAuthority('ROLE_RECRUTEUR')")
public ResponseEntity<OffreEmploiDTO> createOffre(@RequestBody OffreEmploiDTO offreDTO) {
    // Logique métier
}

@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    // Logique métier
}
```

---

## 📊 Structure des menus de navigation

### Menu pour CANDIDAT
```
- Accueil
- Tableau de bord (candidat)
- Mes candidatures
- Offres d'emploi
- Compte
  - Profil
  - Mot de passe
  - Déconnexion
```

### Menu pour RECRUTEUR
```
- Accueil
- Tableau de bord recruteur
- Offres d'emploi
- Compte
  - Profil
  - Mot de passe
  - Déconnexion
```

### Menu pour ADMIN
```
- Accueil
- Offres d'emploi
- Entités (dropdown)
  - Candidat
  - Recruteur
  - Offre Emploi
  - Candidature
  - Type Contrat
  - Localisation
  - Competence
- Administration (dropdown)
  - Authority
  - Gestion des utilisateurs
  - Métriques
  - Diagnostics
  - Configuration
  - Logs
  - API
- Compte
  - Profil
  - Mot de passe
  - Déconnexion
```

---

## 🎨 Pages avec design moderne

### ✅ Page de connexion
- Design deux colonnes
- Bannière avec statistiques
- Redirection automatique selon le rôle après login
- Liens directs vers inscription candidat/recruteur

### ✅ Page d'inscription
- Sélecteur de type (Candidat/Recruteur)
- Formulaire adaptatif selon le type
- Champs spécifiques candidat: téléphone, adresse, CV
- Champs spécifiques recruteur: nom entreprise, secteur

### ✅ Tableaux de bord
- Dashboard candidat: offres récentes, mes candidatures
- Dashboard recruteur: créer offre, gérer offres, voir candidatures

---

## 🚀 Workflow utilisateur

### Candidat
1. S'inscrit avec type "candidat"
2. Reçoit ROLE_CANDIDAT
3. Accède à son tableau de bord
4. Consulte les offres
5. Postule aux offres
6. Suit ses candidatures

### Recruteur
1. S'inscrit avec type "recruteur"
2. Reçoit ROLE_RECRUTEUR
3. Accède à son tableau de bord
4. Crée des offres d'emploi
5. Consulte les candidatures
6. Gère ses offres

### Admin
1. Compte créé par un autre admin
2. Reçoit ROLE_ADMIN
3. Gère toute la plateforme
4. Surveille les métriques
5. Modère le contenu

---

## ⚙️ Fichiers modifiés

### Frontend
- ✅ `app/layouts/navbar/navbar.component.html` - Navigation conditionnelle
- ✅ `app/app.routes.ts` - Protection des routes
- ✅ `app/config/authority.constants.ts` - Constantes des rôles
- ✅ `app/login/login.component.ts` - Redirection selon rôle
- ✅ `app/account/register/register.component.html` - Formulaire adaptatif

### Backend
- ✅ `security/AuthoritiesConstants.java` - Constantes des rôles

---

## 🔐 Règles de sécurité

1. **Jamais de logique métier côté client uniquement**
   - Toutes les vérifications de rôle doivent aussi exister côté serveur

2. **Protection en couches**
   - Route protégée (canActivate)
   - Template conditionnel (*jhiHasAnyAuthority)
   - API protégée (@PreAuthorize)

3. **Principe du moindre privilège**
   - Chaque rôle n'a accès qu'à ce dont il a besoin

4. **Séparation des données**
   - Un candidat ne voit que ses candidatures
   - Un recruteur ne voit que ses offres et candidatures reçues
   - L'admin voit tout

---

## 📝 Notes importantes

- Les utilisateurs non authentifiés peuvent **consulter les offres** sur la page d'accueil
- Le menu "Entités" est **réservé aux ADMIN** pour éviter la confusion
- Les dashboards sont **strictement séparés** par rôle
- La page d'inscription **adapte automatiquement** le formulaire selon le type choisi
- Font Awesome est utilisé pour les icônes dans toute l'application
