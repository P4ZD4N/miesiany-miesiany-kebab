import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-awards-section',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './awards-section.component.html',
  styleUrl: './awards-section.component.scss',
})
export class AwardsSectionComponent {}
