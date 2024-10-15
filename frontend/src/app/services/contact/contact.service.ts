import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ContactResponse, UpdatedContactResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { UpdatedContactRequest } from '../../requests/requests';
import { LangService } from '../lang/lang.service';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  private apiUrl = 'http://localhost:8080/api/v1/contact';

  constructor(private http: HttpClient, private langService: LangService) {}

  getContacts(): Observable<ContactResponse[]> {

    return this.http.get<ContactResponse[]>(`${this.apiUrl}/contacts`, { withCredentials: true });
  }

  updateContact(contact: UpdatedContactRequest): Observable<UpdatedContactResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<UpdatedContactResponse>(`${this.apiUrl}/update-contact`, contact, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  handleError(error: HttpErrorResponse) {

    let errorMessages: { [key: string]: string } = {};

    if (error.error && typeof error.error === 'object') {
      errorMessages = error.error;
    } else {
      console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
    }

    return throwError(() => ({ errorMessages }));
  }
}
