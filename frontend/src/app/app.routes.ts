import { Routes } from '@angular/router';
import { HomeLayoutComponent } from '../app/components/home/home-layout-component';
import { AuthLayoutComponent } from '../app/components/authentication/authentication-layout-component';
import { AuthenticationComponent } from '../app/components/authentication/authentication/authentication.component'

export const routes: Routes = [
    { path: '', component: HomeLayoutComponent },
    { path: 'login', component: AuthLayoutComponent, children: [
        { path: '', component: AuthenticationComponent }
    ]}
];
