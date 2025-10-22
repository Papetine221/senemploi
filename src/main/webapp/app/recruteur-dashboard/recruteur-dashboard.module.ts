import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RecruteurDashboardComponent } from './recruteur-dashboard.component';

@NgModule({
  declarations: [RecruteurDashboardComponent],
  imports: [CommonModule, ReactiveFormsModule],
  exports: [RecruteurDashboardComponent], // <== crucial si utilisé ailleurs
})
export class RecruteurDashboardModule {}