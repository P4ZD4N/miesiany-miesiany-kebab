import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LangService {

  private languageChangedSubject = new Subject<void>();
  languageChanged$ = this.languageChangedSubject.asObservable();
  private _currentLang: string = 'pl';

  get currentLang(): string {
    return this._currentLang;
  }

  set currentLang(lang: string) {
    this._currentLang = lang;
  }

  notifyLanguageChange() {
    this.languageChangedSubject.next();
  }
}
