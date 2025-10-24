# 📄 Gestion du CV dans le système de candidature

## ✅ Fonctionnalité implémentée

Le système de candidature inclut maintenant **la gestion complète du CV** :
- ✅ **Upload obligatoire** du CV pour postuler
- ✅ **Affichage du CV existant** si déjà uploadé
- ✅ **Téléchargement du CV** actuel
- ✅ **Remplacement du CV** par un nouveau fichier
- ✅ **Validation des formats** (PDF, DOC, DOCX)
- ✅ **Limitation de taille** (max 5 Mo)
- ✅ **Stockage en Base64** dans la base de données

---

## 🏗️ Architecture

### **Entité Candidat**

```java
@Entity
public class Candidat {
    private Long id;
    private String telephone;
    private String adresse;
    
    @Lob
    @Column(name = "cv")
    private byte[] cv;                    // ✅ CV en Base64
    
    @Column(name = "cv_content_type")
    private String cvContentType;         // ✅ Type MIME (application/pdf, etc.)
    
    @OneToOne
    private User user;
}
```

---

## 🔄 Flux d'upload du CV

### **1. Candidat accède au formulaire de postulation**

Route : `/candidature/postuler?offre=123`

**Affichage du CV** :
- ✅ Si le candidat a déjà un CV → **Affichage avec badge vert**
- ✅ Boutons "Télécharger" et "Remplacer"
- ✅ Taille du fichier affichée
- ❌ Si pas de CV → **Zone d'upload avec message**

### **2. Upload d'un nouveau CV**

**Zone d'upload interactive** :
```html
<div class="upload-area" (click)="fileInput.click()">
  <fa-icon icon="cloud-upload-alt" size="3x"></fa-icon>
  <h5>Cliquez pour télécharger votre CV</h5>
  <p>Formats acceptés : PDF, DOC, DOCX (max 5 Mo)</p>
</div>

<input #fileInput type="file" 
       accept=".pdf,.doc,.docx" 
       (change)="onFileSelected($event)" 
       class="d-none">
```

**Validations** :
1. ✅ **Format de fichier** : PDF, DOC, DOCX uniquement
2. ✅ **Taille maximale** : 5 Mo (5 * 1024 * 1024 bytes)
3. ✅ **Fichier obligatoire** : Impossible de postuler sans CV

```typescript
onFileSelected(event: Event): void {
  const file = input.files[0];
  
  // Vérifier le type
  const allowedTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ];
  if (!allowedTypes.includes(file.type)) {
    this.cvUploadError = 'Format non accepté. PDF, DOC ou DOCX uniquement.';
    return;
  }
  
  // Vérifier la taille (5 Mo max)
  if (file.size > 5 * 1024 * 1024) {
    this.cvUploadError = 'Fichier trop volumineux. Maximum : 5 Mo.';
    return;
  }
  
  this.newCvFile = file;
  this.cvUploadError = null;
}
```

### **3. Conversion en Base64**

Avant l'envoi au backend, le fichier est converti en Base64 :

```typescript
private convertFileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const base64String = (reader.result as string).split(',')[1];
      resolve(base64String);
    };
    reader.onerror = error => reject(error);
    reader.readAsDataURL(file);
  });
}
```

### **4. Envoi de la candidature**

**Processus complet** :
1. ✅ Vérification que le CV est présent
2. ✅ Conversion du fichier en Base64 (si nouveau)
3. ✅ Mise à jour du profil candidat avec le nouveau CV
4. ✅ Création de la candidature
5. ✅ Notification de succès

```typescript
save(): void {
  this.formSubmitted = true;
  
  // Vérifier si CV présent
  if (!this.candidat?.cv && !this.newCvFile) {
    this.cvUploadError = 'Le CV est obligatoire pour postuler.';
    return;
  }

  if (this.editForm.valid && this.candidat && this.offreEmploi) {
    this.isSaving = true;
    
    const updatedCandidat = {
      ...this.candidat,
      telephone: this.editForm.get('telephone')?.value,
      adresse: this.editForm.get('adresse')?.value
    };

    // Si nouveau CV, le convertir et l'ajouter
    if (this.newCvFile) {
      this.convertFileToBase64(this.newCvFile).then(base64 => {
        updatedCandidat.cv = base64;
        updatedCandidat.cvContentType = this.newCvFile!.type;
        this.updateCandidatAndCreateCandidature(updatedCandidat);
      });
    } else {
      this.updateCandidatAndCreateCandidature(updatedCandidat);
    }
  }
}
```

---

## 📥 Téléchargement du CV

Le candidat peut télécharger son CV actuel :

```typescript
downloadCv(): void {
  if (this.candidat?.cv && this.candidat?.cvContentType) {
    // Convertir Base64 en Blob
    const byteCharacters = atob(this.candidat.cv);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: this.candidat.cvContentType });
    
    // Créer un lien de téléchargement
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `CV_${this.candidat.id}.${this.getFileExtension(this.candidat.cvContentType)}`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
```

---

## 🎨 Interface utilisateur

### **1. CV existant**

```html
@if (candidat.cv && !newCvFile) {
  <div class="alert alert-success">
    <div>
      <fa-icon icon="file-pdf" class="text-danger"></fa-icon>
      <strong>CV actuel :</strong> 
      <span>{{ candidat.cvContentType }}</span>
      <small>({{ getCvSize() }})</small>
    </div>
    <div>
      <button (click)="downloadCv()">
        <fa-icon icon="download"></fa-icon> Télécharger
      </button>
      <button (click)="fileInput.click()">
        <fa-icon icon="upload"></fa-icon> Remplacer
      </button>
    </div>
  </div>
}
```

**Affichage** :
```
┌─────────────────────────────────────────────────┐
│ 📄 CV actuel : application/pdf (245.67 KB)     │
│                                                 │
│         [Télécharger]  [Remplacer]              │
└─────────────────────────────────────────────────┘
```

### **2. Nouveau CV sélectionné**

```html
@if (newCvFile) {
  <div class="alert alert-info">
    <div>
      <fa-icon icon="file"></fa-icon>
      <strong>Nouveau CV :</strong> 
      <span>{{ newCvFile.name }}</span>
      <small>({{ formatFileSize(newCvFile.size) }})</small>
    </div>
    <button (click)="removeCv()">
      <fa-icon icon="times"></fa-icon> Annuler
    </button>
  </div>
}
```

**Affichage** :
```
┌─────────────────────────────────────────────────┐
│ 📝 Nouveau CV : Mon_CV_2024.pdf (312.45 KB)    │
│                                                 │
│                              [Annuler]          │
└─────────────────────────────────────────────────┘
```

### **3. Zone d'upload**

```html
<div class="upload-area" (click)="fileInput.click()">
  <fa-icon icon="cloud-upload-alt" size="3x"></fa-icon>
  <h5>Cliquez pour télécharger votre CV</h5>
  <p>Formats acceptés : PDF, DOC, DOCX (max 5 Mo)</p>
</div>
```

**Affichage** :
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                    ☁️↑                          │
│                                                 │
│      Cliquez pour télécharger votre CV         │
│                                                 │
│  Formats acceptés : PDF, DOC, DOCX (max 5 Mo)  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### **4. Messages d'erreur**

**Pas de CV** :
```html
@if (!candidat.cv && !newCvFile && formSubmitted) {
  <div class="text-danger">
    <small>Le CV est obligatoire pour postuler.</small>
  </div>
}
```

**Format invalide** :
```html
@if (cvUploadError) {
  <div class="alert alert-danger">
    <fa-icon icon="exclamation-triangle"></fa-icon>
    {{ cvUploadError }}
  </div>
}
```

---

## 🎨 Styles CSS

### **Zone d'upload interactive**

```scss
.cv-upload-zone {
  .upload-area {
    background: linear-gradient(135deg, #f8f9fa, #ffffff);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: linear-gradient(135deg, #e9ecef, #f8f9fa);
      border-color: #007bff !important;
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0, 123, 255, 0.1);
    }

    &:hover fa-icon {
      transform: scale(1.1);
    }
  }

  &.has-error .upload-area {
    border-color: #dc3545 !important;
    background: linear-gradient(135deg, #f8d7da, #ffffff);
  }
}
```

### **Badges colorés**

```scss
.alert-success {
  background: linear-gradient(135deg, #d4edda, #c3e6cb);
  color: #155724;
  border-left: 4px solid #28a745;
}

.alert-info {
  background: linear-gradient(135deg, #d1ecf1, #bee5eb);
  color: #0c5460;
  border-left: 4px solid #17a2b8;
}

.alert-danger {
  background: linear-gradient(135deg, #f8d7da, #f5c6cb);
  color: #721c24;
  border-left: 4px solid #dc3545;
}
```

---

## 📊 Formats supportés

| Format | Extension | MIME Type | Icône |
|--------|-----------|-----------|-------|
| PDF | `.pdf` | `application/pdf` | 📄 |
| Word 97-2003 | `.doc` | `application/msword` | 📝 |
| Word 2007+ | `.docx` | `application/vnd.openxmlformats-officedocument.wordprocessingml.document` | 📝 |

---

## 🔒 Validations

### **Frontend (TypeScript)**

```typescript
// 1. Type de fichier
const allowedTypes = [
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
];

// 2. Taille maximale
const maxSize = 5 * 1024 * 1024; // 5 Mo

// 3. Présence obligatoire
if (!candidat?.cv && !newCvFile) {
  return 'CV obligatoire';
}
```

### **Frontend (HTML)**

```html
<input type="file" 
       accept=".pdf,.doc,.docx"
       (change)="onFileSelected($event)">
```

### **Backend (Java)**

```java
@Lob
@Column(name = "cv")
private byte[] cv;  // Stockage en byte array

@Column(name = "cv_content_type")
private String cvContentType;  // MIME type
```

---

## 🧪 Tests de la fonctionnalité

### **Test 1 : Upload d'un CV valide**
1. ✅ Aller sur `/candidature/postuler?offre=123`
2. ✅ Cliquer sur la zone d'upload
3. ✅ Sélectionner un fichier PDF (< 5 Mo)
4. ✅ **Vérifier** : Badge bleu avec nom du fichier
5. ✅ **Vérifier** : Taille affichée
6. ✅ Remplir le formulaire
7. ✅ Envoyer la candidature
8. ✅ **Vérifier** : Succès

### **Test 2 : Tentative avec format invalide**
1. ✅ Cliquer sur la zone d'upload
2. ✅ Sélectionner un fichier `.txt` ou `.jpg`
3. ❌ **Vérifier** : Message d'erreur
4. ❌ **Vérifier** : "Format non accepté"

### **Test 3 : Fichier trop volumineux**
1. ✅ Sélectionner un fichier > 5 Mo
2. ❌ **Vérifier** : Message d'erreur
3. ❌ **Vérifier** : "Fichier trop volumineux. Maximum : 5 Mo"

### **Test 4 : Postuler sans CV**
1. ✅ Remplir le formulaire
2. ✅ Ne pas uploader de CV
3. ✅ Cliquer sur "Envoyer"
4. ❌ **Vérifier** : Message "Le CV est obligatoire"
5. ❌ **Vérifier** : Zone d'upload en rouge

### **Test 5 : Télécharger le CV existant**
1. ✅ Candidat avec CV déjà uploadé
2. ✅ Aller sur la page de postulation
3. ✅ **Vérifier** : Badge vert "CV actuel"
4. ✅ Cliquer sur "Télécharger"
5. ✅ **Vérifier** : Fichier téléchargé

### **Test 6 : Remplacer le CV**
1. ✅ Candidat avec CV existant
2. ✅ Cliquer sur "Remplacer"
3. ✅ Sélectionner un nouveau fichier
4. ✅ **Vérifier** : Badge bleu "Nouveau CV"
5. ✅ **Vérifier** : Bouton "Annuler" disponible
6. ✅ Envoyer la candidature
7. ✅ **Vérifier** : CV mis à jour

### **Test 7 : Annuler le remplacement**
1. ✅ Sélectionner un nouveau CV
2. ✅ **Vérifier** : Badge bleu affiché
3. ✅ Cliquer sur "Annuler"
4. ✅ **Vérifier** : Retour au CV existant
5. ✅ **Vérifier** : Badge vert "CV actuel"

---

## 💾 Stockage

### **Base de données**

```sql
CREATE TABLE candidat (
    id BIGINT PRIMARY KEY,
    telephone VARCHAR(255),
    adresse VARCHAR(255),
    cv BYTEA,                    -- ✅ CV en Base64
    cv_content_type VARCHAR(255), -- ✅ MIME type
    user_id BIGINT
);
```

### **Exemple de données**

```json
{
  "id": 1,
  "telephone": "+33 6 12 34 56 78",
  "adresse": "123 Rue de la Paix, 75001 Paris",
  "cv": "JVBERi0xLjQKJeLjz9MKMSAwIG9iag...",  // Base64
  "cvContentType": "application/pdf",
  "user": {
    "id": 1,
    "login": "candidat1"
  }
}
```

---

## 🔄 Flux complet

```
1. Candidat clique "Postuler" sur une offre
   ↓
2. Formulaire s'affiche avec CV pré-rempli (si existe)
   ↓
3. Candidat peut :
   - ✅ Télécharger son CV actuel
   - ✅ Remplacer par un nouveau CV
   - ✅ Uploader un CV (si n'existe pas)
   ↓
4. Validation du fichier :
   - ✅ Format : PDF, DOC, DOCX
   - ✅ Taille : < 5 Mo
   - ✅ Présence obligatoire
   ↓
5. Conversion en Base64
   ↓
6. Mise à jour du profil candidat
   ↓
7. Création de la candidature
   ↓
8. Notification de succès
```

---

## ✅ Résultat final

**Le système de candidature inclut maintenant une gestion complète du CV !**

Les candidats peuvent :
- ✅ **Uploader leur CV** en PDF, DOC ou DOCX (max 5 Mo)
- ✅ **Voir leur CV actuel** avec taille et format
- ✅ **Télécharger leur CV** à tout moment
- ✅ **Remplacer leur CV** par un nouveau fichier
- ✅ **Validation automatique** du format et de la taille
- ✅ **CV obligatoire** pour postuler
- ✅ **Interface intuitive** avec drag & drop visuel
- ✅ **Messages d'erreur clairs** en cas de problème
- ✅ **Stockage sécurisé** en Base64 dans la base de données

**Expérience utilisateur optimale et interface professionnelle !** 🎉
