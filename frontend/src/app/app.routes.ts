import { Routes } from '@angular/router';
import { HomeLayoutComponent } from '../app/components/home/home-layout-component';
import { AuthLayoutComponent } from '../app/components/authentication/authentication-layout-component';
import { AuthenticationComponent } from '../app/components/authentication/authentication/authentication.component'
import { HoursComponent } from './components/opening-hours/hours/hours.component';
import { OpeningHoursLayoutComponent } from './components/opening-hours/opening-hours-layout.component';

export const routes: Routes = [
    { path: '', component: HomeLayoutComponent },
    { path: 'login', component: AuthLayoutComponent, children: [
        { path: '', component: AuthenticationComponent }
    ]},
    { path: 'opening-hours', component: OpeningHoursLayoutComponent, children: [
        { path: '', component: HoursComponent }
    ]}, 
    { path: 'godziny-otwarcia', component: OpeningHoursLayoutComponent, children: [
        { path: '', component: HoursComponent }
    ]}
];
