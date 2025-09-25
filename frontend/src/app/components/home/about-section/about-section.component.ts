import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-about-section',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './about-section.component.html',
  styleUrl: './about-section.component.scss',
})
export class AboutSectionComponent {}
