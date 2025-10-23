import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-login',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export default class LoginComponent implements OnInit, AfterViewInit {
  username = viewChild.required<ElementRef>('username');

  authenticationError = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    // if already authenticated then navigate to appropriate page
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        // Si l'utilisateur est un candidat, rediriger vers son tableau de bord
        if (this.accountService.hasAnyAuthority('ROLE_CANDIDAT')) {
          this.router.navigate(['/candidat-dashboard']);
        } else {
          // Sinon, rediriger vers la page d'accueil
          this.router.navigate(['']);
        }
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

  login(): void {
    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.authenticationError.set(false);
        if (!this.router.getCurrentNavigation()) {
          // There were no routing during login (eg from navigationToStoredUrl)
          // Si l'utilisateur est un candidat, rediriger vers son tableau de bord
          if (this.accountService.hasAnyAuthority('ROLE_CANDIDAT')) {
            this.router.navigate(['/candidat-dashboard']);
          } else {
            // Sinon, rediriger vers la page d'accueil
            this.router.navigate(['']);
          }
        }
      },
      error: () => this.authenticationError.set(true),
    });
  }
}
