import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../common/navbar/navbar.component';
import { HeroSectionComponent } from '../home/hero-section/hero-section.component';
import { AboutSectionComponent } from '../home/about-section/about-section.component';
import { AwardsSectionComponent } from '../home/awards-section/awards-section.component';
import { LocationSectionComponent } from '../home/location-section/location-section.component';
import { FooterComponent } from '../common/footer/footer.component';
import { TranslateModule } from '@ngx-translate/core';

@Component({
    selector: 'app-home-layout',
    standalone: true,
    imports: [
      RouterOutlet,
      NavbarComponent,
      HeroSectionComponent,
      AboutSectionComponent,
      AwardsSectionComponent,
      LocationSectionComponent,
      FooterComponent,
      TranslateModule,
    ],
    template: `
      <app-navbar></app-navbar>
      <app-hero-section></app-hero-section>
      <app-about-section></app-about-section>
      <app-awards-section></app-awards-section>
      <app-location-section></app-location-section>
      <app-footer></app-footer>
    `
  })
  export class HomeLayoutComponent {}