import { AfterViewInit, Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';
import SharedModule from 'app/shared/shared.module';
import PasswordStrengthBarComponent from '../password/password-strength-bar/password-strength-bar.component';
import { RegisterService } from './register.service';

@Component({
  selector: 'jhi-register',
  imports: [SharedModule, RouterModule, FormsModule, ReactiveFormsModule, PasswordStrengthBarComponent],
  templateUrl: './register.component.html',
})
export default class RegisterComponent implements AfterViewInit {
  login = viewChild.required<ElementRef>('login');

  doNotMatch = signal(false);
  error = signal(false);
  errorEmailExists = signal(false);
  errorUserExists = signal(false);
  success = signal(false);
  userType: string | null = null;
  cvFile: File | null = null;
  cvContentType: string | null = null;

  registerForm = new FormGroup({
    login: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    telephone: new FormControl('', { nonNullable: true }),
    adresse: new FormControl('', { nonNullable: true }),
    cv: new FormControl('', { nonNullable: true }),
  });

  private readonly translateService = inject(TranslateService);
  private readonly registerService = inject(RegisterService);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.userType = params.get('type');
    });
  }

  ngAfterViewInit(): void {
    this.login().nativeElement.focus();
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.cvFile = input.files[0];
      this.cvContentType = this.cvFile.type;
    }
  }

  register(): void {
    this.doNotMatch.set(false);
    this.error.set(false);
    this.errorEmailExists.set(false);
    this.errorUserExists.set(false);

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch.set(true);
    } else {
      const { login, email, telephone, adresse } = this.registerForm.getRawValue();
      
      // Traiter le fichier CV si présent
      if (this.cvFile) {
        const reader = new FileReader();
        reader.readAsDataURL(this.cvFile);
        reader.onload = () => {
          // Extraire la partie base64 en supprimant le préfixe (ex: data:application/pdf;base64,)
          const base64String = reader.result as string;
          const base64Content = base64String.split(',')[1];
          
          this.registerService
            .save({
              login,
              email,
              password,
              langKey: this.translateService.currentLang,
              telephone,
              adresse,
              cv: base64Content,
              cvContentType: this.cvContentType,
              type: this.userType
            })
            .subscribe({
              next: () => this.success.set(true),
              error: response => this.processError(response)
            });
        };
      } else {
        // Pas de fichier CV
        this.registerService
          .save({
            login,
            email,
            password,
            langKey: this.translateService.currentLang,
            telephone,
            adresse,
            cv: null,
            cvContentType: null,
            type: this.userType
          })
          .subscribe({
            next: () => this.success.set(true),
            error: response => this.processError(response)
          });
      }
    }
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists.set(true);
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists.set(true);
    } else {
      this.error.set(true);
    }
  }
}
