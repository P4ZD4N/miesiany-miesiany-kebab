import { Routes } from '@angular/router';
import { HomeLayoutComponent } from '../app/components/home/home-layout-component';
import { AuthLayoutComponent } from '../app/components/authentication/authentication-layout-component';
import { AuthenticationComponent } from '../app/components/authentication/authentication/authentication.component';
import { HoursComponent } from './components/opening-hours/hours/hours.component';
import { OpeningHoursLayoutComponent } from './components/opening-hours/opening-hours-layout.component';
import { ManagerPanelLayoutComponent } from './components/manager-panel/manager-panel-layout.component';
import { ManagerPanelComponent } from './components/manager-panel/panel/manager-panel.component';
import { EmployeePanelLayoutComponent } from './components/employee-panel/employee-panel-layout.component';
import { EmployeePanelComponent } from './components/employee-panel/panel/employee-panel.component';
import { MenuLayoutComponent } from './components/menu/menu.layout.component';
import { MenuComponent } from './components/menu/menu/menu.component';
import { ContactLayoutComponent } from './components/contact/contact-layout-component';
import { ContactComponent } from './components/contact/contact/contact.component';
import { JobsLayoutComponent } from './components/jobs/jobs.layout.component';
import { JobsComponent } from './components/jobs/jobs/jobs.component';
import { PromotionsLayoutComponent } from './components/promotions/promotions-layout-component';
import { PromotionsComponent } from './components/promotions/promotions/promotions.component';
import { UnsubscribeLayoutComponent } from './components/unsubscribe/unsubscribe-layout-component';
import { UnsubscribeComponent } from './components/unsubscribe/unsubscribe/unsubscribe.component';
import { TrackOrderLayoutComponent } from './components/track-order/track-order-layout-component';
import { TrackOrderComponent } from './components/track-order/track-order/track-order.component';
import { OrderManagementLayoutComponent } from './components/order-management/order-management-layout-component';
import { OrderManagementComponent } from './components/order-management/order-management/order-management.component';
import { DiscountCodeManagementLayoutComponent } from './components/discount-code-management/discount-code-management-layout.component';
import { DiscountCodeManagementComponent } from './components/discount-code-management/discount-code-management/discount-code-management.component';
import { OrderStatusDisplayComponent } from './components/order-status-display/order-status-display/order-status-display.component';
import { OrderStatusDisplayLayoutComponent } from './components/order-status-display/order-status-display-layout.component';
import { WorkScheduleLayoutComponent } from './components/work-schedule/work-schedule-layout-component';
import { WorkScheduleComponent } from './components/work-schedule/work-schedule/work-schedule.component';
import { EmployeeManagementComponent } from './components/employee-management/employee-management/employee-management.component';
import { EmployeeManagementLayoutComponent } from './components/employee-management/employee-management.component';
import { PaymentsComponent } from './components/payments/payments/payments.component';
import { PaymentsLayoutComponent } from './components/payments/payments-layout.component';
import { NotFoundComponent } from './components/not-found/not-found/not-found.component';
import { NotFoundLayoutComponent } from './components/not-found/not-found-layout.component';

export const routes: Routes = [
  { path: '', component: HomeLayoutComponent },
  {
    path: 'login',
    component: AuthLayoutComponent,
    children: [{ path: '', component: AuthenticationComponent }],
  },
  {
    path: 'opening-hours',
    component: OpeningHoursLayoutComponent,
    children: [{ path: '', component: HoursComponent }],
  },
  {
    path: 'manager-panel',
    component: ManagerPanelLayoutComponent,
    children: [{ path: '', component: ManagerPanelComponent }],
  },
  {
    path: 'employee-panel',
    component: EmployeePanelLayoutComponent,
    children: [{ path: '', component: EmployeePanelComponent }],
  },
  {
    path: 'menu',
    component: MenuLayoutComponent,
    children: [{ path: '', component: MenuComponent }],
  },
  {
    path: 'contact',
    component: ContactLayoutComponent,
    children: [{ path: '', component: ContactComponent }],
  },
  {
    path: 'jobs',
    component: JobsLayoutComponent,
    children: [{ path: '', component: JobsComponent }],
  },
  {
    path: 'promotions',
    component: PromotionsLayoutComponent,
    children: [{ path: '', component: PromotionsComponent }],
  },
  {
    path: 'unsubscribe',
    component: UnsubscribeLayoutComponent,
    children: [{ path: '', component: UnsubscribeComponent }],
  },
  {
    path: 'track-order',
    component: TrackOrderLayoutComponent,
    children: [{ path: '', component: TrackOrderComponent }],
  },
  {
    path: 'order-management',
    component: OrderManagementLayoutComponent,
    children: [{ path: '', component: OrderManagementComponent }],
  },
  {
    path: 'discount-code-management',
    component: DiscountCodeManagementLayoutComponent,
    children: [{ path: '', component: DiscountCodeManagementComponent }],
  },
  {
    path: 'order-status-display',
    component: OrderStatusDisplayLayoutComponent,
    children: [{ path: '', component: OrderStatusDisplayComponent }],
  },
  {
    path: 'work-schedule',
    component: WorkScheduleLayoutComponent,
    children: [{ path: '', component: WorkScheduleComponent }],
  },
  {
    path: 'employee-management',
    component: EmployeeManagementLayoutComponent,
    children: [{ path: '', component: EmployeeManagementComponent }],
  },
  {
    path: 'payments',
    component: PaymentsLayoutComponent,
    children: [{ path: '', component: PaymentsComponent }],
  },
  {
    path: 'not-found',
    component: NotFoundLayoutComponent,
    children: [{ path: '', component: NotFoundComponent }],
  },
  {
    path: '**',
    redirectTo: 'not-found',
    pathMatch: 'full',
  },
];
