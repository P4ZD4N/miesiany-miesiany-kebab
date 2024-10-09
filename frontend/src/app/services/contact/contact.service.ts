import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ContactResponse } from '../../responses/responses';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  private apiUrl = 'http://localhost:8080/api/v1/contact';

  constructor(private http: HttpClient) {}

  getContacts(): Observable<ContactResponse[]> {

    return this.http.get<ContactResponse[]>(`${this.apiUrl}/contacts`, { withCredentials: true });
  }
}
