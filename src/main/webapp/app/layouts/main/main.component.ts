import { Component, OnInit, Renderer2, RendererFactory2, inject, signal } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { AccountService } from 'app/core/auth/account.service';
import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import FooterComponent from '../footer/footer.component';
import PageRibbonComponent from '../profiles/page-ribbon.component';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, FooterComponent, PageRibbonComponent],
})
export default class MainComponent implements OnInit {
  private readonly renderer: Renderer2;

  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly translateService = inject(TranslateService);
  private readonly rootRenderer = inject(RendererFactory2);

  // Signal pour contrôler l'affichage de la navbar
  // Par défaut false, sera true uniquement pour les pages admin
  showNavbar = signal<boolean>(false);

  constructor() {
    this.renderer = this.rootRenderer.createRenderer(document.querySelector('html'), null);
  }

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(document.querySelector('html'), 'lang', langChangeEvent.lang);
    });

    // Écouter les changements de route pour masquer/afficher la navbar
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateNavbarVisibility();
      });

    // Vérifier la route initiale
    this.updateNavbarVisibility();
  }

  private updateNavbarVisibility(): void {
    const currentRoute = this.router.routerState.root;
    const pageRibbon = this.getPageRibbonValue(currentRoute);
    
    // Afficher la navbar uniquement si pageRibbon n'est pas explicitement false
    // Cela signifie que seules les routes admin (sans pageRibbon: false) auront la navbar
    this.showNavbar.set(pageRibbon !== false);
  }

  private getPageRibbonValue(route: any): boolean | undefined {
    if (route.firstChild) {
      return this.getPageRibbonValue(route.firstChild);
    }
    return route.snapshot?.data?.pageRibbon;
  }
}
