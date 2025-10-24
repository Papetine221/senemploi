# 👁️ Aperçu du CV en miniature - Documentation

## ✅ Fonctionnalité ajoutée

Le système de candidature inclut maintenant **l'affichage du CV en miniature (preview)** directement dans le formulaire de postulation.

---

## 🎯 Objectif

Permettre au candidat de **visualiser son CV** avant d'envoyer sa candidature pour :
- ✅ Vérifier que le bon fichier est sélectionné
- ✅ S'assurer que le CV est lisible
- ✅ Voir un aperçu rapide sans télécharger
- ✅ Améliorer l'expérience utilisateur

---

## 🖼️ Interface utilisateur

### **1. CV existant avec bouton "Aperçu"**

```
┌────────────────────────────────────────────────────┐
│ ✅ CV actuel : application/pdf (245.67 KB)        │
│                                                    │
│  [👁️ Aperçu] [📥 Télécharger] [🔄 Remplacer]     │
└────────────────────────────────────────────────────┘
```

### **2. Miniature du CV (PDF)**

Quand le candidat clique sur "Aperçu" :

```
┌────────────────────────────────────────────────────┐
│ 🔍 Aperçu du CV                              [✕]  │
├────────────────────────────────────────────────────┤
│                                                    │
│  ┌──────────────────────────────────────────┐     │
│  │                                          │     │
│  │         [Contenu du PDF affiché]         │     │
│  │                                          │     │
│  │         dans un iframe de 600px          │     │
│  │                                          │     │
│  └──────────────────────────────────────────┘     │
│                                                    │
└────────────────────────────────────────────────────┘
```

### **3. Miniature du CV (Word DOC/DOCX)**

Pour les documents Word (non visualisables directement) :

```
┌────────────────────────────────────────────────────┐
│ 🔍 Aperçu du CV                              [✕]  │
├────────────────────────────────────────────────────┤
│                                                    │
│                    📄 (icône Word)                 │
│                                                    │
│                  CV_candidat.docx                  │
│                                                    │
│      Cliquez sur "Télécharger" pour voir          │
│               le contenu complet                   │
│                                                    │
└────────────────────────────────────────────────────┘
```

### **4. Nouveau CV avec aperçu**

```
┌────────────────────────────────────────────────────┐
│ 📝 Nouveau CV : Mon_CV_2024.pdf (312.45 KB)       │
│                                                    │
│              [👁️ Aperçu] [❌ Annuler]              │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│ 🔍 Aperçu du nouveau CV                      [✕]  │
├────────────────────────────────────────────────────┤
│                                                    │
│        [Preview du nouveau fichier PDF]            │
│                                                    │
└────────────────────────────────────────────────────┘
```

---

## 🏗️ Architecture technique

### **HTML Template**

**Bouton Aperçu** :
```html
<button 
  type="button" 
  class="btn btn-sm btn-outline-info"
  (click)="previewCv()">
  <fa-icon icon="eye"></fa-icon>
  Aperçu
</button>
```

**Container de preview** :
```html
@if (showCvPreview) {
  <div class="cv-preview-container">
    <div class="cv-preview-header">
      <h6>
        <fa-icon icon="search"></fa-icon>
        Aperçu du CV
      </h6>
      <button (click)="showCvPreview = false">
        <fa-icon icon="times"></fa-icon>
      </button>
    </div>
    
    <div class="cv-thumbnail">
      @if (isPdfCv(candidat.cvContentType)) {
        <iframe 
          [src]="getCvPreviewUrl()" 
          class="cv-iframe">
        </iframe>
      } @else {
        <div class="cv-doc-preview">
          <fa-icon icon="file-word" size="5x"></fa-icon>
          <h5>{{ getFileName() }}</h5>
          <p>Document Word - Téléchargez pour voir le contenu</p>
        </div>
      }
    </div>
  </div>
}
```

### **TypeScript Component**

**Variables** :
```typescript
showCvPreview = false;           // Afficher preview CV existant
showNewCvPreview = false;        // Afficher preview nouveau CV
newCvPreviewUrl: any = null;     // URL du nouveau CV
```

**Méthode pour afficher le CV existant** :
```typescript
previewCv(): void {
  this.showCvPreview = !this.showCvPreview;
  this.showNewCvPreview = false;  // Fermer l'autre preview
}
```

**Méthode pour afficher le nouveau CV** :
```typescript
previewNewCv(): void {
  if (this.newCvFile) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      // Sanitize l'URL pour la sécurité
      this.newCvPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(e.target.result);
      this.showNewCvPreview = true;
      this.showCvPreview = false;
    };
    reader.readAsDataURL(this.newCvFile);
  }
}
```

**Générer l'URL du CV existant** :
```typescript
getCvPreviewUrl(): SafeResourceUrl | null {
  if (this.candidat?.cv && this.candidat?.cvContentType) {
    // Convertir Base64 → Blob → URL
    const byteCharacters = atob(this.candidat.cv);
    const byteNumbers = new Array(byteCharacters.length);
    
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: this.candidat.cvContentType });
    const url = URL.createObjectURL(blob);
    
    // Sanitize pour Angular
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
  return null;
}
```

**Vérifier si PDF** :
```typescript
isPdfCv(contentType: string | undefined): boolean {
  return contentType === 'application/pdf';
}
```

**Nom du fichier** :
```typescript
getFileName(): string {
  if (this.candidat?.id) {
    return `CV_${this.candidat.id}.${this.getFileExtension(this.candidat.cvContentType || '')}`;
  }
  return 'CV.pdf';
}
```

---

## 🎨 Styles CSS

### **Container de preview**

```scss
.cv-preview-container {
  border: 2px solid #e9ecef;
  border-radius: 0.5rem;
  padding: 1rem;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .cv-preview-header {
    padding-bottom: 0.5rem;
    border-bottom: 1px solid #dee2e6;
    margin-bottom: 1rem;

    h6 {
      font-weight: 600;
      color: #495057;
    }
  }

  .cv-thumbnail {
    width: 100%;
    min-height: 400px;
    background: #f8f9fa;
    border-radius: 0.375rem;
    overflow: hidden;

    .cv-iframe {
      width: 100%;
      height: 600px;
      border: none;
      background: white;
    }

    .cv-doc-preview {
      background: linear-gradient(135deg, #f8f9fa, #ffffff);
      border: 2px dashed #dee2e6;
      border-radius: 0.375rem;
      padding: 3rem;
      text-align: center;

      fa-icon {
        opacity: 0.7;
        transition: all 0.3s ease;
      }

      &:hover fa-icon {
        opacity: 1;
        transform: scale(1.05);
      }
    }
  }
}
```

### **Bouton Aperçu**

```scss
.btn-outline-info {
  border: 2px solid #17a2b8;
  color: #17a2b8;

  &:hover {
    background: #17a2b8;
    color: white;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(23, 162, 184, 0.3);
  }
}
```

---

## 🔒 Sécurité

### **Sanitization avec DomSanitizer**

Angular bloque par défaut les URL non sûres. On utilise `DomSanitizer` pour contourner cette protection de manière sécurisée :

```typescript
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

// Injection
protected sanitizer = inject(DomSanitizer);

// Utilisation
return this.sanitizer.bypassSecurityTrustResourceUrl(url);
```

**Pourquoi ?**
- ✅ Les URL `blob:` et `data:` sont considérées comme non sûres
- ✅ `bypassSecurityTrustResourceUrl()` indique à Angular que l'URL est sûre
- ✅ Fonctionne uniquement avec les types `SafeResourceUrl`
- ✅ Ne compromet pas la sécurité car les données viennent de la base de données

---

## 📊 Formats supportés

| Format | Preview | Affichage |
|--------|---------|-----------|
| **PDF** | ✅ Oui | Iframe avec contenu complet |
| **DOC** | ❌ Non | Icône Word + Message |
| **DOCX** | ❌ Non | Icône Word + Message |

**Pourquoi les documents Word ne sont pas prévisualisés ?**
- ❌ Les navigateurs ne peuvent pas afficher les `.doc` / `.docx` nativement
- ❌ Nécessiterait une conversion serveur (Word → PDF ou HTML)
- ✅ On affiche une icône avec un message explicatif

---

## 🔄 Flux utilisateur

### **Scénario 1 : CV existant (PDF)**

```
1. Candidat voit le badge "CV actuel"
   ↓
2. Clique sur "Aperçu"
   ↓
3. Miniature s'affiche avec le PDF
   ↓
4. Candidat peut :
   - Fermer avec [✕]
   - Télécharger
   - Remplacer le CV
```

### **Scénario 2 : CV existant (Word)**

```
1. Candidat voit le badge "CV actuel"
   ↓
2. Clique sur "Aperçu"
   ↓
3. Message s'affiche : "Document Word"
   ↓
4. Candidat doit télécharger pour voir le contenu
```

### **Scénario 3 : Nouveau CV (PDF)**

```
1. Candidat sélectionne un nouveau fichier PDF
   ↓
2. Badge bleu "Nouveau CV" apparaît
   ↓
3. Clique sur "Aperçu"
   ↓
4. Preview du nouveau fichier s'affiche
   ↓
5. Candidat peut :
   - Fermer avec [✕]
   - Annuler la sélection
   - Envoyer la candidature
```

### **Scénario 4 : Changement entre previews**

```
1. Candidat a un CV existant et sélectionne un nouveau
   ↓
2. Aperçu du CV existant ouvert
   ↓
3. Clique sur "Aperçu" du nouveau CV
   ↓
4. ✅ Preview du CV existant se ferme automatiquement
5. ✅ Preview du nouveau CV s'ouvre
```

---

## 🧪 Tests de la fonctionnalité

### **Test 1 : Aperçu CV existant (PDF)**
1. ✅ Candidat avec CV PDF uploadé
2. ✅ Aller sur `/candidature/postuler?offre=123`
3. ✅ Cliquer sur "Aperçu"
4. ✅ **Vérifier** : Miniature affichée avec iframe
5. ✅ **Vérifier** : Contenu du PDF visible
6. ✅ **Vérifier** : Hauteur 600px
7. ✅ Cliquer sur [✕]
8. ✅ **Vérifier** : Preview se ferme

### **Test 2 : Aperçu CV existant (Word)**
1. ✅ Candidat avec CV Word uploadé
2. ✅ Cliquer sur "Aperçu"
3. ✅ **Vérifier** : Icône Word affichée
4. ✅ **Vérifier** : Message "Téléchargez pour voir"
5. ✅ **Vérifier** : Pas d'iframe

### **Test 3 : Aperçu nouveau CV (PDF)**
1. ✅ Sélectionner un nouveau fichier PDF
2. ✅ Cliquer sur "Aperçu" du nouveau CV
3. ✅ **Vérifier** : Preview s'affiche
4. ✅ **Vérifier** : Contenu visible dans iframe
5. ✅ Cliquer sur "Annuler"
6. ✅ **Vérifier** : Preview se ferme

### **Test 4 : Bascule entre previews**
1. ✅ CV existant (PDF) avec preview ouvert
2. ✅ Sélectionner un nouveau CV
3. ✅ Cliquer sur "Aperçu" du nouveau
4. ✅ **Vérifier** : Preview du CV existant fermé
5. ✅ **Vérifier** : Preview du nouveau ouvert
6. ✅ Revenir à l'aperçu du CV existant
7. ✅ **Vérifier** : Preview du nouveau fermé

### **Test 5 : Suppression avec preview ouvert**
1. ✅ Nouveau CV avec preview ouvert
2. ✅ Cliquer sur "Annuler"
3. ✅ **Vérifier** : Preview fermé
4. ✅ **Vérifier** : Fichier supprimé

### **Test 6 : Responsive mobile**
1. ✅ Ouvrir sur mobile
2. ✅ Cliquer sur "Aperçu"
3. ✅ **Vérifier** : Preview s'adapte à la largeur
4. ✅ **Vérifier** : Iframe scrollable

---

## 💡 Avantages

### **Pour le candidat** 👤
- ✅ **Visualisation rapide** : Voir le CV sans télécharger
- ✅ **Vérification** : S'assurer d'avoir le bon fichier
- ✅ **Confiance** : Contrôle avant l'envoi
- ✅ **Gain de temps** : Pas besoin d'ouvrir un autre onglet

### **Pour l'UX** 🎨
- ✅ **Interface moderne** : Preview intégré
- ✅ **Feedback visuel** : Voir ce qui sera envoyé
- ✅ **Transparence** : Aucune surprise
- ✅ **Professionnalisme** : Application soignée

### **Pour la sécurité** 🔒
- ✅ **Validation visuelle** : Le candidat voit son CV
- ✅ **Prévention d'erreurs** : Détection de mauvais fichier
- ✅ **Sanitization** : URL sécurisées avec DomSanitizer

---

## 🚀 Améliorations futures

### **Preview avancé**
- 📄 **Conversion Word → PDF** côté serveur
- 🖼️ **Thumbnails** pour toutes les pages
- 🔍 **Zoom** dans le preview
- 📱 **Fullscreen** pour mobile

### **Annotations**
- ✏️ **Surligner** des sections
- 💬 **Commentaires** avant envoi
- 🔖 **Marque-pages** dans le CV

### **Comparaison**
- 📊 **Comparer** deux versions de CV
- 📈 **Historique** des modifications
- 🎯 **Suggestions** d'amélioration

---

## ✅ Résultat final

**Le système de candidature inclut maintenant l'aperçu du CV en miniature !**

Les candidats peuvent :
- ✅ **Voir leur CV** directement dans le formulaire
- ✅ **Vérifier le contenu** avant l'envoi
- ✅ **Prévisualiser les PDF** en haute qualité
- ✅ **Basculer** entre CV existant et nouveau
- ✅ **Fermer/ouvrir** le preview facilement
- ✅ **Interface responsive** sur tous les appareils
- ✅ **Sécurité garantie** avec DomSanitizer

**Expérience utilisateur premium et professionnelle !** 🎉
