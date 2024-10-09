import { Component, OnInit } from '@angular/core';
import tt from '@tomtom-international/web-sdk-maps';
import { environment } from '../../../../environments/environment.development';
import { ContactResponse } from '../../../responses/responses';
import { ContactService } from '../../../services/contact/contact.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [TranslateModule, CommonModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
})

export class ContactComponent implements OnInit {

  contacts: ContactResponse[] = [];
  kebabCoordinates: [number, number] = [18.978145609677117, 50.73271996898827]

  constructor(private contactService: ContactService) {}

  ngOnInit() {
    const map = tt.map({
      key: environment.MAP_API_KEY,
      container: "map",
      center: this.kebabCoordinates,
      zoom: 15
    })

    map.addControl(new tt.NavigationControl())

    new tt.Marker()
      .setLngLat(this.kebabCoordinates)
      .addTo(map);

    this.loadContacts();
  }

  loadContacts(): void {
    this.contactService.getContacts().subscribe(
      (data: ContactResponse[]) => {
        this.contacts = data;
      },
      (error) => {
        console.log('Error loading contacts', error);
      }
    );
  }

  getTelephone(): ContactResponse[] {
    return this.contacts.filter(contact => contact.contact_type === 'TELEPHONE');
  }

  formatPhone(telephone: string): string {
    return `+48 ${telephone.slice(0, 3)} ${telephone.slice(3, 6)} ${telephone.slice(6, 9)}`;
  }

  getEmail(): ContactResponse[] {
    return this.contacts.filter(contact => contact.contact_type === 'EMAIL');
  }
}
