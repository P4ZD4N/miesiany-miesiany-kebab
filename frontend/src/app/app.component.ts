import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/common/navbar/navbar.component';

import { TranslateModule } from '@ngx-translate/core';
import { HeroSectionComponent } from './components/home/hero-section/hero-section.component';
import { AboutSectionComponent } from './components/home/about-section/about-section.component';
import { AwardsSectionComponent } from './components/home/awards-section/awards-section.component';
import { LocationSectionComponent } from './components/home/location-section/location-section.component';
import { FooterComponent } from './components/common/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    NavbarComponent, 
    HeroSectionComponent, 
    AboutSectionComponent, 
    AwardsSectionComponent, 
    LocationSectionComponent,
    FooterComponent, 
    TranslateModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';
}
