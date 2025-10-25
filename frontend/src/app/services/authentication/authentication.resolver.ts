import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { SessionCheckResponse } from '../../responses/responses';

@Injectable({
  providedIn: 'root',
})
export class AuthResolverService implements Resolve<SessionCheckResponse> {
  constructor(private authService: AuthenticationService) {}

  resolve(): Observable<SessionCheckResponse> {
    return this.authService.ensureSessionChecked();
  }
}
