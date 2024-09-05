import { Component, HostListener, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { TranslateService } from '@ngx-translate/core';
import { LangService } from '../../../services/lang/lang.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, TranslateModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})

export class NavbarComponent implements OnInit {
  currentFlag: string = '🇺🇸';

  constructor(
    private translate: TranslateService,
    private langService: LangService
  ) {}

  ngOnInit() {
    this.currentFlag = this.langService.currentLang === 'pl' ? '🇺🇸' : '🇵🇱';
  }

  switchLanguage() {
    if (this.translate.currentLang === 'pl') {
      this.translate.use('en');
      this.currentFlag = '🇵🇱';
      this.langService.currentLang = 'en';
    } else {
      this.translate.use('pl');
      this.currentFlag = '🇺🇸';
      this.langService.currentLang = 'pl';
    }
    this.langService.notifyLanguageChange();
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    const navbar = document.querySelector('.navbar');
    if (navbar) {
      if (window.scrollY > 30) { 
        navbar.classList.add('shrink');
      } else {
        navbar.classList.remove('shrink');
      }
    }
  }

  getCurrentFlag(): string {
    return this.currentFlag;
  }
}
