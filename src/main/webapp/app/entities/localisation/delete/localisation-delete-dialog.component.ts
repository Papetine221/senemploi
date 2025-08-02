import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ILocalisation } from '../localisation.model';
import { LocalisationService } from '../service/localisation.service';

@Component({
  templateUrl: './localisation-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class LocalisationDeleteDialogComponent {
  localisation?: ILocalisation;

  protected localisationService = inject(LocalisationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.localisationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
