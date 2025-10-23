# 🧪 Tests de redirection par rôle

## Test de connexion - Redirection automatique

### ✅ Comportement attendu après connexion

| Rôle | Page de destination | Route |
|------|-------------------|-------|
| **ROLE_ADMIN** | Gestion des utilisateurs | `/admin/user-management` |
| **ROLE_RECRUTEUR** | Tableau de bord recruteur | `/recruteur-dashboard` |
| **ROLE_CANDIDAT** | Tableau de bord candidat | `/candidat-dashboard` |
| **ROLE_USER** (standard) | Page d'accueil | `/` |

---

## 🔍 Scénarios de test

### Scénario 1 : Connexion ADMIN
1. ✅ Aller sur `/login`
2. ✅ Se connecter avec un compte ADMIN (ex: admin/admin)
3. ✅ **Vérifier** : Redirection automatique vers `/admin/user-management`
4. ✅ **Vérifier** : Menu "Administration" et "Entités" visibles dans la navbar

### Scénario 2 : Connexion CANDIDAT
1. ✅ Aller sur `/login`
2. ✅ Se connecter avec un compte CANDIDAT
3. ✅ **Vérifier** : Redirection automatique vers `/candidat-dashboard`
4. ✅ **Vérifier** : Menu "Tableau de bord" et "Mes candidatures" visibles
5. ✅ **Vérifier** : Menu "Entités" et "Administration" NON visibles

### Scénario 3 : Connexion RECRUTEUR
1. ✅ Aller sur `/login`
2. ✅ Se connecter avec un compte RECRUTEUR
3. ✅ **Vérifier** : Redirection automatique vers `/recruteur-dashboard`
4. ✅ **Vérifier** : Menu "Tableau de bord recruteur" visible
5. ✅ **Vérifier** : Menu "Entités" et "Administration" NON visibles

### Scénario 4 : Utilisateur déjà connecté
1. ✅ Se connecter avec n'importe quel compte
2. ✅ Essayer d'accéder à `/login` directement
3. ✅ **Vérifier** : Redirection automatique vers la page appropriée selon le rôle

---

## 🔐 Tests de restriction d'accès

### Test 1 : Candidat tente d'accéder au dashboard recruteur
1. Se connecter en tant que CANDIDAT
2. Tenter d'accéder à `/recruteur-dashboard`
3. **Attendu** : Accès refusé ou redirection vers `/candidat-dashboard`

### Test 2 : Recruteur tente d'accéder au dashboard candidat
1. Se connecter en tant que RECRUTEUR
2. Tenter d'accéder à `/candidat-dashboard`
3. **Attendu** : Accès refusé ou redirection vers `/recruteur-dashboard`

### Test 3 : Candidat/Recruteur tente d'accéder au menu Admin
1. Se connecter en tant que CANDIDAT ou RECRUTEUR
2. Tenter d'accéder à `/admin/user-management`
3. **Attendu** : Accès refusé (403 Forbidden)

### Test 4 : Non authentifié tente d'accéder à un dashboard
1. Sans connexion, essayer d'accéder à `/candidat-dashboard`
2. **Attendu** : Redirection vers `/login`

---

## 📋 Checklist de vérification

### Après connexion ADMIN ✅
- [ ] URL est `/admin/user-management`
- [ ] Menu "Administration" visible
- [ ] Menu "Entités" visible
- [ ] Peut accéder à toutes les pages

### Après connexion CANDIDAT ✅
- [ ] URL est `/candidat-dashboard`
- [ ] Menu "Tableau de bord" visible
- [ ] Menu "Mes candidatures" visible
- [ ] Menu "Entités" NON visible
- [ ] Menu "Administration" NON visible

### Après connexion RECRUTEUR ✅
- [ ] URL est `/recruteur-dashboard`
- [ ] Menu "Tableau de bord recruteur" visible
- [ ] Menu "Entités" NON visible
- [ ] Menu "Administration" NON visible

---

## 🛠️ Code de redirection (login.component.ts)

```typescript
private redirectBasedOnRole(): void {
  if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
    // Admin → Page d'administration
    this.router.navigate(['/admin/user-management']);
  } else if (this.accountService.hasAnyAuthority('ROLE_RECRUTEUR')) {
    // Recruteur → Tableau de bord recruteur
    this.router.navigate(['/recruteur-dashboard']);
  } else if (this.accountService.hasAnyAuthority('ROLE_CANDIDAT')) {
    // Candidat → Tableau de bord candidat
    this.router.navigate(['/candidat-dashboard']);
  } else {
    // Utilisateur standard → Page d'accueil
    this.router.navigate(['']);
  }
}
```

---

## 🎯 Points d'attention

1. **Ordre des vérifications** : Important ! On vérifie ADMIN en premier car un admin pourrait avoir plusieurs rôles
2. **Hiérarchie des rôles** : ADMIN > RECRUTEUR > CANDIDAT > USER
3. **Navigation en cours** : La méthode vérifie `getCurrentNavigation()` pour éviter les conflits de routing
4. **Double vérification** : La redirection s'applique dans `ngOnInit()` ET dans `login()` pour couvrir tous les cas

---

## 🚀 Commandes de test

### Créer des utilisateurs de test (via console backend)
```sql
-- Créer un candidat de test
INSERT INTO jhi_user (login, password_hash, activated) 
VALUES ('candidat1', '$2a$10$...', true);

-- Créer un recruteur de test
INSERT INTO jhi_user (login, password_hash, activated) 
VALUES ('recruteur1', '$2a$10$...', true);
```

### Tester via l'interface
1. Créer des comptes via `/account/register`
2. Se connecter et vérifier la redirection
3. Vérifier les menus visibles/cachés

---

## 📊 Matrice de test complète

| Action | ADMIN | RECRUTEUR | CANDIDAT | Non auth |
|--------|-------|-----------|----------|----------|
| Accès `/login` | Redirige dashboard | Redirige dashboard | Redirige dashboard | ✅ OK |
| Accès `/candidat-dashboard` | ✅ OK | ❌ Refusé | ✅ OK | ❌ → login |
| Accès `/recruteur-dashboard` | ✅ OK | ✅ OK | ❌ Refusé | ❌ → login |
| Accès `/admin/*` | ✅ OK | ❌ Refusé | ❌ Refusé | ❌ → login |
| Voir menu "Entités" | ✅ Visible | ❌ Caché | ❌ Caché | ❌ Caché |
| Voir menu "Administration" | ✅ Visible | ❌ Caché | ❌ Caché | ❌ Caché |
| Voir "Mes candidatures" | ✅ Visible | ❌ Caché | ✅ Visible | ❌ Caché |
| Voir "Tableau de bord recruteur" | ✅ Visible | ✅ Visible | ❌ Caché | ❌ Caché |

---

## ✅ Résultat attendu

Après avoir implémenté ces modifications, chaque utilisateur doit être automatiquement redirigé vers SA page spécifique dès la connexion, offrant une expérience utilisateur fluide et sécurisée.
