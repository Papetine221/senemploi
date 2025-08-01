import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRecruteur } from '../recruteur.model';
import { RecruteurService } from '../service/recruteur.service';

@Component({
  templateUrl: './recruteur-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RecruteurDeleteDialogComponent {
  recruteur?: IRecruteur;

  protected recruteurService = inject(RecruteurService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.recruteurService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
