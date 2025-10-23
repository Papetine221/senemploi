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
        this.redirectBasedOnRole();
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
          this.redirectBasedOnRole();
        }
      },
      error: () => this.authenticationError.set(true),
    });
  }

  /**
   * Redirige l'utilisateur vers sa page spécifique selon son rôle
   */
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
}
