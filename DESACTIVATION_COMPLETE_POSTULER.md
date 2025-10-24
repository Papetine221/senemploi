# ✅ Désactivation complète du bouton "Postuler"

## 🎯 Objectif

Désactiver le bouton "Postuler" dans **toutes les pages** où il apparaît si le candidat a déjà postulé à l'offre.

---

## 📍 Emplacements du bouton "Postuler"

Le bouton "Postuler" apparaît dans **3 endroits** :

### **1. Dashboard Candidat** ✅
- **Fichier** : `candidat-dashboard.component.html`
- **Contexte** : Cartes d'offres récentes
- **Bouton** : `[✈️ Postuler]` → `[✓ Déjà postulé]`

### **2. Page de détails de l'offre** ✅
- **Fichier** : `offre-emploi-detail.component.html`
- **Contexte** : Bouton principal "Postuler maintenant"
- **Bouton** : `[✈️ Postuler maintenant]` → `[✓ Déjà postulé]`

### **3. Liste des offres** ✅
- **Fichier** : `offre-emploi.component.html`
- **Contexte** : Tableau des offres d'emploi
- **Bouton** : `[✈️ Postuler]` → `[✓ Déjà postulé]`

---

## 🏗️ Implémentation

### **1. Dashboard Candidat**

#### **TypeScript** (`candidat-dashboard.component.ts`)

```typescript
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';
import { ICandidature } from 'app/entities/candidature/candidature.model';

export class CandidatDashboardComponent implements OnInit {
  private candidatureService = inject(CandidatureService);
  candidatures = signal<ICandidature[]>([]);

  ngOnInit(): void {
    // Charger les candidatures du candidat
    this.candidatureService.query().subscribe({
      next: (response) => {
        if (response.body) {
          this.candidatures.set(response.body);
        }
      }
    });
  }

  hasAlreadyApplied(offreId: number): boolean {
    return this.candidatures().some(candidature => candidature.offre?.id === offreId);
  }
}
```

#### **HTML** (`candidat-dashboard.component.html`)

```html
<div class="job-card-actions">
  <a [routerLink]="['/offre-emploi', offre.id, 'view']" class="btn-view-details">
    Voir détails
  </a>
  
  @if (hasAlreadyApplied(offre.id)) {
    <button class="btn-apply btn-applied" disabled>
      <fa-icon icon="check-circle"></fa-icon>
      Déjà postulé
    </button>
  } @else {
    <a [routerLink]="['/candidature/postuler']" [queryParams]="{ offre: offre.id }" class="btn-apply">
      <fa-icon icon="paper-plane"></fa-icon>
      Postuler
    </a>
  }
</div>
```

---

### **2. Page de détails de l'offre**

#### **TypeScript** (`offre-emploi-detail.component.ts`)

```typescript
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';
import { ICandidature } from 'app/entities/candidature/candidature.model';

export class OffreEmploiDetailComponent implements OnInit {
  protected candidatureService = inject(CandidatureService);
  candidatures = signal<ICandidature[]>([]);
  
  ngOnInit(): void {
    // Charger les candidatures du candidat si c'est un candidat
    if (this.hasAnyAuthority('ROLE_CANDIDAT')) {
      this.candidatureService.query().subscribe({
        next: (response) => {
          if (response.body) {
            this.candidatures.set(response.body);
          }
        }
      });
    }
  }
  
  hasAlreadyApplied(): boolean {
    const offreId = this.offreEmploi()?.id;
    if (!offreId) return false;
    return this.candidatures().some(candidature => candidature.offre?.id === offreId);
  }
}
```

#### **HTML** (`offre-emploi-detail.component.html`)

```html
<ng-container *ngIf="hasAnyAuthority('ROLE_CANDIDAT')">
  @if (hasAlreadyApplied()) {
    <button 
      type="button" 
      class="btn btn-secondary btn-lg w-100 mb-3"
      disabled>
      <fa-icon icon="check-circle"></fa-icon>
      <span>Déjà postulé</span>
    </button>
  } @else {
    <button 
      type="button" 
      [routerLink]="['/candidature/postuler']" 
      [queryParams]="{offre: offreEmploiRef.id}" 
      class="btn btn-success btn-lg w-100 mb-3">
      <fa-icon icon="paper-plane"></fa-icon>
      <span>Postuler maintenant</span>
    </button>
  }
</ng-container>
```

---

### **3. Liste des offres**

#### **TypeScript** (`offre-emploi.component.ts`)

```typescript
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';
import { ICandidature } from 'app/entities/candidature/candidature.model';

export class OffreEmploiComponent implements OnInit {
  protected candidatureService = inject(CandidatureService);
  candidatures = signal<ICandidature[]>([]);
  
  ngOnInit(): void {
    // Charger les candidatures du candidat si c'est un candidat
    if (this.hasAnyAuthority('ROLE_CANDIDAT')) {
      this.candidatureService.query().subscribe({
        next: (response) => {
          if (response.body) {
            this.candidatures.set(response.body);
          }
        }
      });
    }
    
    // ... reste du code
  }

  hasAlreadyApplied(offreEmploi: IOffreEmploi): boolean {
    return this.candidatures().some(candidature => candidature.offre?.id === offreEmploi.id);
  }
}
```

#### **HTML** (`offre-emploi.component.html`)

```html
<ng-container *ngIf="hasAnyAuthority('ROLE_CANDIDAT')">
  @if (hasAlreadyApplied(offreEmploi)) {
    <button class="btn btn-sm btn-secondary" disabled>
      <fa-icon icon="check-circle"></fa-icon>
      <span class="ms-1 d-none d-md-inline">Déjà postulé</span>
    </button>
  } @else {
    <a [routerLink]="['/candidature/postuler']" [queryParams]="{offre: offreEmploi.id}" class="btn btn-sm btn-success">
      <fa-icon icon="paper-plane"></fa-icon>
      <span class="ms-1 d-none d-md-inline">Postuler</span>
    </a>
  }
</ng-container>
```

---

## 🎨 Styles CSS

### **Dashboard Candidat** (`candidat-dashboard.component.scss`)

```scss
.btn-apply {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(17, 153, 142, 0.3);
  }

  &.btn-applied {
    background: linear-gradient(135deg, #6c757d, #5a6268);
    cursor: not-allowed;
    opacity: 0.7;

    &:hover {
      transform: none;
      box-shadow: none;
    }
  }
}
```

### **Page de détails** (inline styles)

```css
.btn-secondary:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}
```

### **Liste des offres** (inline styles)

```css
.btn-secondary:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}
```

---

## 🔄 Flux complet

```
1. Candidat se connecte
   ↓
2. Système charge toutes ses candidatures
   ↓
3. Pour chaque offre affichée :
   ├─ Vérification : candidature existe ?
   │  ├─ ✅ OUI → Bouton "Déjà postulé" (gris, désactivé)
   │  └─ ❌ NON → Bouton "Postuler" (vert, actif)
   ↓
4. Candidat clique sur "Postuler" (si actif)
   ↓
5. Remplit et envoie le formulaire
   ↓
6. ✅ Redirection vers /candidature/mes-candidatures
   ↓
7. Retour à n'importe quelle page
   ↓
8. ✅ Bouton "Postuler" → "Déjà postulé" (désactivé)
```

---

## 📊 Comparaison avant/après

### **Dashboard Candidat**

| État | Avant | Après |
|------|-------|-------|
| **Pas postulé** | [✈️ Postuler] (vert) | [✈️ Postuler] (vert) |
| **Déjà postulé** | [✈️ Postuler] (vert) ❌ | [✓ Déjà postulé] (gris) ✅ |

### **Page de détails**

| État | Avant | Après |
|------|-------|-------|
| **Pas postulé** | [✈️ Postuler maintenant] (vert) | [✈️ Postuler maintenant] (vert) |
| **Déjà postulé** | [✈️ Postuler maintenant] (vert) ❌ | [✓ Déjà postulé] (gris) ✅ |

### **Liste des offres**

| État | Avant | Après |
|------|-------|-------|
| **Pas postulé** | [✈️ Postuler] (vert) | [✈️ Postuler] (vert) |
| **Déjà postulé** | [✈️ Postuler] (vert) ❌ | [✓ Déjà postulé] (gris) ✅ |

---

## 🧪 Tests de validation

### **Test 1 : Dashboard**
1. ✅ Se connecter en tant que CANDIDAT
2. ✅ Postuler à une offre depuis le dashboard
3. ✅ Retourner au dashboard
4. ✅ **Vérifier** : Bouton "Déjà postulé" (gris)
5. ✅ **Vérifier** : Bouton désactivé

### **Test 2 : Page de détails**
1. ✅ Aller sur `/offre-emploi/123/view`
2. ✅ **Vérifier** : Bouton "Postuler maintenant" (vert)
3. ✅ Postuler à l'offre
4. ✅ Retourner sur `/offre-emploi/123/view`
5. ✅ **Vérifier** : Bouton "Déjà postulé" (gris)
6. ✅ **Vérifier** : Bouton désactivé

### **Test 3 : Liste des offres**
1. ✅ Aller sur `/offre-emploi`
2. ✅ Postuler à l'offre #1
3. ✅ Retourner sur `/offre-emploi`
4. ✅ **Vérifier** : Offre #1 → "Déjà postulé" (gris)
5. ✅ **Vérifier** : Autres offres → "Postuler" (vert)

### **Test 4 : Cohérence entre pages**
1. ✅ Postuler depuis le dashboard
2. ✅ Aller sur la page de détails
3. ✅ **Vérifier** : "Déjà postulé"
4. ✅ Aller sur la liste des offres
5. ✅ **Vérifier** : "Déjà postulé"
6. ✅ **Vérifier** : Cohérence sur les 3 pages

### **Test 5 : Rafraîchissement**
1. ✅ Postuler à une offre
2. ✅ Rafraîchir la page (F5)
3. ✅ **Vérifier** : Bouton "Déjà postulé" toujours affiché
4. ✅ **Vérifier** : État persistant

---

## 💡 Points clés

### **Chargement des candidatures**
- ✅ Chargé une seule fois au `ngOnInit()`
- ✅ Stocké dans un `signal<ICandidature[]>`
- ✅ Accessible dans tout le composant

### **Vérification**
```typescript
hasAlreadyApplied(offreId: number): boolean {
  return this.candidatures().some(candidature => 
    candidature.offre?.id === offreId
  );
}
```

### **Affichage conditionnel**
```html
@if (hasAlreadyApplied(offre.id)) {
  <!-- Bouton désactivé -->
} @else {
  <!-- Bouton actif -->
}
```

---

## ✅ Résultat final

**Le bouton "Postuler" est maintenant désactivé dans TOUTES les pages !**

### **3 pages couvertes** :
1. ✅ **Dashboard Candidat** - Cartes d'offres récentes
2. ✅ **Page de détails** - Bouton "Postuler maintenant"
3. ✅ **Liste des offres** - Tableau des offres

### **Fonctionnalités** :
- ✅ **Vérification automatique** au chargement
- ✅ **Bouton désactivé** si déjà postulé
- ✅ **Icône check-circle** pour feedback visuel
- ✅ **Couleur grise** pour indiquer l'état
- ✅ **Curseur `not-allowed`** au survol
- ✅ **Cohérence** entre toutes les pages
- ✅ **État persistant** après rafraîchissement

**Système complet et production-ready !** 🎉
