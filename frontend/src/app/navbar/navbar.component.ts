import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})

export class NavbarComponent {
  currentFlag: string = '🇺🇸';

  constructor(private translate: TranslateService) {}

  switchLanguage() {
    if (this.translate.currentLang === 'pl') {
      this.translate.use('en');
      this.currentFlag = '🇵🇱';
    } else {
      this.translate.use('pl');
      this.currentFlag = '🇺🇸';
    }
  }
}
