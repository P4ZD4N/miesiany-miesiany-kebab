import { Routes } from '@angular/router';
import { HomeLayoutComponent } from '../app/components/home/home-layout-component';
import { AuthLayoutComponent } from '../app/components/authentication/authentication-layout-component';
import { AuthenticationComponent } from '../app/components/authentication/authentication/authentication.component'
import { HoursComponent } from './components/opening-hours/hours/hours.component';
import { OpeningHoursLayoutComponent } from './components/opening-hours/opening-hours-layout.component';
import { ManagerPanelLayoutComponent } from './components/manager-panel/manager-panel-layout.component';
import { ManagerPanelComponent } from './components/manager-panel/panel/manager-panel.component';
import { EmployeePanelLayoutComponent } from './components/employee-panel/employee-panel-layout.component';
import { EmployeePanelComponent } from './components/employee-panel/panel/employee-panel.component';

export const routes: Routes = [
    { path: '', component: HomeLayoutComponent },
    { path: 'login', component: AuthLayoutComponent, children: [
        { path: '', component: AuthenticationComponent }
    ]},
    { path: 'opening-hours', component: OpeningHoursLayoutComponent, children: [
        { path: '', component: HoursComponent }
    ]}, 
    { path: 'manager-panel', component: ManagerPanelLayoutComponent, children: [
        { path: '', component: ManagerPanelComponent }
    ]}, 
    { path: 'employee-panel', component: EmployeePanelLayoutComponent, children: [
        { path: '', component: EmployeePanelComponent }
    ]}, 
];
