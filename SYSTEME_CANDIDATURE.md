# 📝 Système de Candidature - Documentation Complète

## ✅ Fonctionnalités implémentées

Le système de candidature permet aux candidats de :
1. ✅ **Postuler à une offre d'emploi** avec leur profil pré-rempli
2. ✅ **Voir toutes leurs candidatures** avec leur statut
3. ✅ **Éditer leurs informations** avant de postuler
4. ✅ **Suivre l'évolution** de leurs candidatures

---

## 🏗️ Architecture

### **Entité Candidature**

```java
@Entity
public class Candidature {
    private Long id;
    private String lettreMotivation;      // Lettre de motivation (obligatoire)
    private Instant datePostulation;      // Date de postulation
    private StatutCandidature statut;     // EN_ATTENTE, ACCEPTEE, REFUSEE
    
    @ManyToOne
    private Candidat candidat;            // Le candidat qui postule
    
    @ManyToOne
    private OffreEmploi offre;            // L'offre visée
}
```

### **Statuts de candidature**

```typescript
enum StatutCandidature {
  EN_ATTENTE = 'EN_ATTENTE',      // 🟡 En attente de traitement
  ACCEPTEE = 'ACCEPTEE',          // 🟢 Candidature acceptée
  REFUSEE = 'REFUSEE'             // 🔴 Candidature refusée
}
```

---

## 🔄 Flux de candidature

### **1. Candidat clique sur "Postuler"**

**Dashboard Candidat** :
```html
<a [routerLink]="['/candidature/postuler']" 
   [queryParams]="{ offre: offre.id }" 
   class="btn-apply">
  <fa-icon icon="paper-plane"></fa-icon>
  Postuler
</a>
```

**Route** : `/candidature/postuler?offre=123`

### **2. Affichage du formulaire de candidature**

Le composant `CandidaturePostulerComponent` :
- ✅ Charge l'offre d'emploi sélectionnée
- ✅ Récupère le profil du candidat connecté
- ✅ Pré-remplit le formulaire avec ses informations
- ✅ Permet la modification avant envoi

**Formulaire pré-rempli** :
```typescript
editForm = this.fb.group({
  lettreMotivation: ['', [Validators.required, Validators.minLength(50)]],
  telephone: [candidat.telephone, [Validators.required]],
  adresse: [candidat.adresse, [Validators.required]]
});
```

### **3. Validation et envoi**

**Étapes** :
1. ✅ Validation du formulaire (lettre motivation min 50 caractères)
2. ✅ Mise à jour du profil candidat (téléphone, adresse)
3. ✅ Création de la candidature avec statut `EN_ATTENTE`
4. ✅ Notification de succès
5. ✅ Redirection vers `/offre-emploi`

**Code backend** :
```java
@PostMapping("/api/candidatures")
public ResponseEntity<CandidatureDTO> createCandidature(@Valid @RequestBody CandidatureDTO candidatureDTO) {
    candidatureDTO.setStatut(StatutCandidature.EN_ATTENTE);
    candidatureDTO.setDatePostulation(Instant.now());
    CandidatureDTO result = candidatureService.save(candidatureDTO);
    return ResponseEntity.created(new URI("/api/candidatures/" + result.getId())).body(result);
}
```

### **4. Consultation des candidatures**

**Route** : `/candidature/mes-candidatures`

Le composant `MesCandidaturesComponent` :
- ✅ Récupère toutes les candidatures du candidat connecté
- ✅ Affiche le statut avec badge coloré
- ✅ Permet de voir les détails de chaque candidature
- ✅ Affiche des statistiques (en attente, acceptées, refusées)

---

## 📁 Structure des fichiers

### **Frontend (Angular)**

```
src/main/webapp/app/entities/candidature/
├── candidature.model.ts                    # Modèle TypeScript
├── candidature.routes.ts                   # Configuration des routes
├── postuler/
│   ├── candidature-postuler.component.ts   # ✅ Composant de postulation
│   ├── candidature-postuler.component.html # ✅ Formulaire pré-rempli
│   └── candidature-postuler.component.scss # ✅ Styles
├── mes-candidatures/
│   ├── mes-candidatures.component.ts       # ✅ Liste des candidatures
│   ├── mes-candidatures.component.html     # ✅ Affichage avec statuts
│   └── mes-candidatures.component.scss     # ✅ Styles
└── service/
    └── candidature.service.ts              # Service API
```

### **Backend (Spring Boot)**

```
src/main/java/com/mycompany/myapp/
├── domain/
│   ├── Candidature.java                    # ✅ Entité JPA
│   └── enumeration/
│       └── StatutCandidature.java          # Enum des statuts
├── repository/
│   ├── CandidatureRepository.java          # Repository JPA
│   └── CandidatRepository.java             # ✅ + findByUserLogin()
├── service/
│   ├── CandidatureService.java             # Interface service
│   ├── CandidatService.java                # ✅ + findByUserLogin()
│   └── impl/
│       ├── CandidatureServiceImpl.java     # Implémentation
│       └── CandidatServiceImpl.java        # ✅ + findByUserLogin()
└── web/rest/
    ├── CandidatureResource.java            # REST Controller
    └── CandidatResource.java               # ✅ + GET /by-user-login/{login}
```

---

## 🔐 Sécurité et autorisations

### **Routes protégées**

```typescript
// app.routes.ts
{
  path: 'candidature/postuler',
  canActivate: [UserRouteAccessService],
  data: {
    authorities: ['ROLE_CANDIDAT'],  // ✅ Réservé aux candidats
    pageRibbon: false
  }
},
{
  path: 'candidature/mes-candidatures',
  canActivate: [UserRouteAccessService],
  data: {
    authorities: ['ROLE_CANDIDAT'],  // ✅ Réservé aux candidats
    pageRibbon: false
  }
}
```

### **Endpoints backend**

```java
// CandidatureResource.java
@PostMapping("/api/candidatures")
@PreAuthorize("hasAnyAuthority('ROLE_CANDIDAT', 'ROLE_ADMIN')")
public ResponseEntity<CandidatureDTO> createCandidature(...) { }

@GetMapping("/api/candidatures")
@PreAuthorize("hasAnyAuthority('ROLE_CANDIDAT', 'ROLE_ADMIN')")
public ResponseEntity<List<CandidatureDTO>> getAllCandidatures(...) { }

// CandidatResource.java
@GetMapping("/api/candidats/by-user-login/{userLogin}")
@PreAuthorize("hasAnyAuthority('ROLE_CANDIDAT', 'ROLE_ADMIN')")
public ResponseEntity<CandidatDTO> getCandidatByUserLogin(...) { }
```

---

## 🎨 Interface utilisateur

### **1. Dashboard Candidat**

**Carte "Mes Candidatures"** :
```html
<div class="card">
  <h5>Mes Candidatures</h5>
  <p>Suivez l'état de vos candidatures envoyées</p>
  <a [routerLink]="['/candidature/mes-candidatures']" class="btn btn-info">
    Voir mes candidatures
  </a>
</div>
```

**Carte d'offre avec bouton "Postuler"** :
```html
<div class="job-card">
  <h3>{{ offre.titre }}</h3>
  <p>{{ offre.description }}</p>
  
  <div class="actions">
    <a [routerLink]="['/offre-emploi', offre.id, 'view']">Voir détails</a>
    <a [routerLink]="['/candidature/postuler']" 
       [queryParams]="{ offre: offre.id }">
      Postuler
    </a>
  </div>
</div>
```

### **2. Page de postulation**

**Formulaire avec informations pré-remplies** :
```html
<form [formGroup]="editForm" (ngSubmit)="save()">
  <!-- Informations de l'offre (lecture seule) -->
  <div class="offre-info">
    <h2>{{ offreEmploi?.titre }}</h2>
    <p>{{ offreEmploi?.description }}</p>
    <span>Salaire: {{ offreEmploi?.salaire }}</span>
  </div>

  <!-- Informations du candidat (modifiables) -->
  <div class="candidat-info">
    <h3>Vos informations</h3>
    <input type="tel" formControlName="telephone" placeholder="Téléphone" />
    <input type="text" formControlName="adresse" placeholder="Adresse" />
  </div>

  <!-- Lettre de motivation -->
  <textarea formControlName="lettreMotivation" 
            placeholder="Rédigez votre lettre de motivation (min 50 caractères)"
            rows="10"></textarea>

  <!-- Actions -->
  <button type="button" (click)="previousState()">Annuler</button>
  <button type="submit" [disabled]="!editForm.valid || isSaving">
    Envoyer ma candidature
  </button>
</form>
```

### **3. Page "Mes candidatures"**

**Liste avec statuts** :
```html
<div class="candidatures-list">
  <!-- Statistiques -->
  <div class="stats">
    <div class="stat-card">
      <h3>{{ getCandidaturesEnAttente() }}</h3>
      <p>En attente</p>
    </div>
    <div class="stat-card">
      <h3>{{ getCandidaturesAcceptees() }}</h3>
      <p>Acceptées</p>
    </div>
    <div class="stat-card">
      <h3>{{ getCandidaturesRefusees() }}</h3>
      <p>Refusées</p>
    </div>
  </div>

  <!-- Liste des candidatures -->
  @for (candidature of candidatures; track candidature.id) {
    <div class="candidature-card">
      <h3>{{ candidature.offre?.titre }}</h3>
      <p>{{ candidature.offre?.recruteur?.nomEntreprise }}</p>
      
      <span [class]="getStatutBadgeClass(candidature.statut)">
        {{ getStatutText(candidature.statut) }}
      </span>
      
      <p>Postulé le: {{ candidature.datePostulation | date:'dd/MM/yyyy' }}</p>
      
      <div class="actions">
        <button (click)="voirOffre(candidature)">Voir l'offre</button>
        <button (click)="voirCandidature(candidature)">Détails</button>
      </div>
    </div>
  }
</div>
```

---

## 🔄 API Endpoints

### **Candidature**

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `POST` | `/api/candidatures` | Créer une candidature | CANDIDAT, ADMIN |
| `GET` | `/api/candidatures` | Lister les candidatures | CANDIDAT, ADMIN |
| `GET` | `/api/candidatures/{id}` | Détails d'une candidature | CANDIDAT, ADMIN |
| `PUT` | `/api/candidatures/{id}` | Mettre à jour | ADMIN |
| `DELETE` | `/api/candidatures/{id}` | Supprimer | ADMIN |

### **Candidat**

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `GET` | `/api/candidats/by-user-login/{login}` | ✅ Récupérer candidat par login | CANDIDAT, ADMIN |
| `GET` | `/api/candidats/{id}` | Détails d'un candidat | CANDIDAT, ADMIN |
| `PUT` | `/api/candidats/{id}` | Mettre à jour profil | CANDIDAT, ADMIN |

---

## 🧪 Tests de la fonctionnalité

### **Test 1 : Postuler à une offre**
1. ✅ Se connecter en tant que CANDIDAT
2. ✅ Accéder au dashboard `/candidat-dashboard`
3. ✅ Cliquer sur "Postuler" sur une offre
4. ✅ **Vérifier** : Redirection vers `/candidature/postuler?offre=X`
5. ✅ **Vérifier** : Formulaire pré-rempli avec téléphone et adresse
6. ✅ **Vérifier** : Informations de l'offre affichées
7. ✅ Rédiger une lettre de motivation (min 50 caractères)
8. ✅ Modifier téléphone/adresse si besoin
9. ✅ Cliquer sur "Envoyer ma candidature"
10. ✅ **Vérifier** : Message de succès
11. ✅ **Vérifier** : Redirection vers `/offre-emploi`

### **Test 2 : Voir ses candidatures**
1. ✅ Se connecter en tant que CANDIDAT
2. ✅ Accéder au dashboard `/candidat-dashboard`
3. ✅ Cliquer sur "Voir mes candidatures"
4. ✅ **Vérifier** : Redirection vers `/candidature/mes-candidatures`
5. ✅ **Vérifier** : Liste des candidatures affichée
6. ✅ **Vérifier** : Statuts colorés (🟡 En attente, 🟢 Acceptée, 🔴 Refusée)
7. ✅ **Vérifier** : Statistiques affichées
8. ✅ Cliquer sur "Voir l'offre"
9. ✅ **Vérifier** : Redirection vers le détail de l'offre

### **Test 3 : Candidat sans profil**
1. ✅ Se connecter avec un compte CANDIDAT sans profil candidat
2. ✅ Essayer de postuler
3. ✅ **Vérifier** : Message d'erreur
4. ✅ **Vérifier** : Redirection vers `/candidat/new` pour créer le profil

### **Test 4 : Validation du formulaire**
1. ✅ Essayer d'envoyer sans lettre de motivation
2. ✅ **Vérifier** : Message "Champ requis"
3. ✅ Rédiger moins de 50 caractères
4. ✅ **Vérifier** : Message "Minimum 50 caractères"
5. ✅ Laisser téléphone vide
6. ✅ **Vérifier** : Message "Téléphone requis"

---

## 🎯 Fonctionnalités clés

### ✅ **Pré-remplissage automatique**
- Téléphone et adresse du candidat sont automatiquement chargés
- Le candidat peut les modifier avant d'envoyer
- Mise à jour du profil lors de l'envoi

### ✅ **Validation complète**
- Lettre de motivation : min 50 caractères, max 2000
- Téléphone : format valide (8-15 chiffres)
- Adresse : min 10 caractères, max 200

### ✅ **Gestion des statuts**
- EN_ATTENTE : Badge jaune 🟡
- ACCEPTEE : Badge vert 🟢
- REFUSEE : Badge rouge 🔴

### ✅ **Statistiques**
- Nombre de candidatures en attente
- Nombre de candidatures acceptées
- Nombre de candidatures refusées

### ✅ **Navigation fluide**
- Depuis dashboard → Postuler → Mes candidatures
- Retour facile avec bouton "Annuler"
- Liens vers détails offre et candidature

---

## 💡 Points importants

### **1. Unicité des candidatures**
⚠️ **À implémenter** : Empêcher un candidat de postuler deux fois à la même offre

```java
// À ajouter dans CandidatureRepository
Optional<Candidature> findByCandidatIdAndOffreId(Long candidatId, Long offreId);
```

### **2. Notifications**
⚠️ **À implémenter** : Notifier le candidat quand le statut change

### **3. CV obligatoire**
⚠️ **À implémenter** : Vérifier que le candidat a uploadé son CV

### **4. Limite de candidatures**
⚠️ **À implémenter** : Limiter le nombre de candidatures simultanées

---

## ✅ Résultat final

**Le système de candidature est maintenant fonctionnel !**

Les candidats peuvent :
- ✅ Postuler facilement avec leurs informations pré-remplies
- ✅ Éditer leurs informations avant d'envoyer
- ✅ Voir toutes leurs candidatures avec leur statut
- ✅ Suivre l'évolution de leurs candidatures
- ✅ Accéder rapidement aux détails des offres

**Expérience utilisateur optimale et interface intuitive !** 🎉
