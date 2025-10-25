import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LangService {
  private languageChangedSubject = new Subject<void>();
  private _currentLang: string = 'pl';

  languageChanged$ = this.languageChangedSubject.asObservable();

  constructor(private translate: TranslateService) {
    const savedLanguage = localStorage.getItem('lang') || 'pl';
    this.setLanguage(savedLanguage);
  }

  get currentLang(): string {
    return this._currentLang;
  }

  set currentLang(lang: string) {
    this._currentLang = lang;
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
    this.notifyLanguageChange();
  }

  notifyLanguageChange(): void {
    this.languageChangedSubject.next();
  }

  private setLanguage(lang: string): void {
    this._currentLang = lang;
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
  }
}
