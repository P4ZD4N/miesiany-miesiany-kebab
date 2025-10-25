import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  catchError,
  map,
  Observable,
  shareReplay,
  tap,
  throwError,
} from 'rxjs';
import { LangService } from '../lang/lang.service';
import { Role } from '../../enums/role.enum';
import { AuthenticationRequest } from '../../requests/requests';
import {
  AuthenticationResponse,
  SessionCheckResponse,
} from '../../responses/responses';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private userRole: Role | null = null;
  private sessionCheck$: Observable<SessionCheckResponse> | null = null;

  constructor(private http: HttpClient, private langService: LangService) {}

  authenticate(
    request: AuthenticationRequest
  ): Observable<AuthenticationResponse> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang,
    });

    return this.http
      .post<AuthenticationResponse>(`${this.apiUrl}/login`, request, {
        headers,
        withCredentials: true,
      })
      .pipe(
        map((response) => {
          this.userRole = response.role;
          return response;
        }),
        catchError(this.handleError)
      );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.userRole = null;
        }),
        catchError(this.handleError)
      );
  }

  isManager(): boolean {
    return this.userRole === Role.MANAGER;
  }

  isEmployee(): boolean {
    return this.userRole === Role.EMPLOYEE;
  }

  ensureSessionChecked(): Observable<SessionCheckResponse> {
    if (!this.sessionCheck$) {
      this.sessionCheck$ = this.http
        .get<SessionCheckResponse>(`${this.apiUrl}/check-session`, {
          withCredentials: true,
        })
        .pipe(
          map((response) => {
            this.userRole = response.role;
            return response;
          }),
          shareReplay({ bufferSize: 1, refCount: true }),
          catchError(this.handleError)
        );
    }

    return this.sessionCheck$;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessages: { [key: string]: string } = {};

    error.error && typeof error.error === 'object'
      ? (errorMessages = error.error)
      : console.error(
          `Backend returned code ${error.status}, body was: ${error.error}`
        );

    return throwError(() => ({ errorMessages }));
  }
}
