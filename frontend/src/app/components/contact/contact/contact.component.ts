import { Component, OnInit } from '@angular/core';
import tt from '@tomtom-international/web-sdk-maps';
import { environment } from '../../../../environments/environment.development';
import { ContactResponse } from '../../../responses/responses';
import { ContactService } from '../../../services/contact/contact.service';
import { Observable, Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { UpdatedContactRequest } from '../../../requests/requests';
import { ContactType } from '../../../enums/contact-type.enum';
import { LangService } from '../../../services/lang/lang.service';
import Swal from 'sweetalert2';
import { fakeAsync } from '@angular/core/testing';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [FormsModule, TranslateModule, CommonModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
})

export class ContactComponent implements OnInit {

  contacts: ContactResponse[] = [];
  errorMessages: { [key: string]: string } = {};
  kebabCoordinates: [number, number] = [18.978145609677117, 50.73271996898827]

  isEditingPhone: boolean = false;
  isEditingEmail: boolean = false;
  isEditing = false;

  phoneValue: string = '';
  emailValue: string = '';

  languageChangeSubscription: Subscription;

  constructor(
    private contactService: ContactService, 
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    });
  }

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
        this.initPhoneAndEmailValues();
      },
      (error) => {
        console.log('Error loading contacts', error);
      }
    );
  }

  toggleEditPhone(): void {
    this.isEditing = true;
    this.isEditingPhone = !this.isEditingPhone;
  }

  hideEditPhoneForm(): void {
    this.hideErrorMessages();
    this.isEditingPhone = false;
    this.isEditing = false;
    this.initPhoneAndEmailValues();
  }

  updatePhone(): void {

    let contactTypeTranslated = this.translate.instant('contact.types.' + ContactType.TELEPHONE);

    let updatedPhone: UpdatedContactRequest = {
        contact_type: ContactType.TELEPHONE,
        new_value: this.phoneValue
    }

    this.contactService.updateContact(updatedPhone).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano kontakt typu '${contactTypeTranslated}'!` : `Successfully updated contact of type '${contactTypeTranslated}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadContacts();
        this.phoneValue = '';
        this.isEditing = false;
        this.hideEditPhoneForm();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }
 
  toggleEditEmail(): void {
    this.isEditing = true;
    this.isEditingEmail = !this.isEditingEmail;
  }

  hideEditEmailForm(): void {
    this.hideErrorMessages();
    this.isEditingEmail = false;
    this.isEditing = false;
    this.initPhoneAndEmailValues();
  }

  updateEmail(): void {

    let contactTypeTranslated = this.translate.instant('contact.types.' + ContactType.EMAIL);

    let updatedEmail: UpdatedContactRequest = {
        contact_type: ContactType.EMAIL,
        new_value: this.emailValue
    }

    this.contactService.updateContact(updatedEmail).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano kontakt typu '${contactTypeTranslated}'!` : `Successfully updated contact of type '${contactTypeTranslated}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadContacts();
        this.emailValue = '';
        this.isEditing = false;
        this.hideEditEmailForm();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
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

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  initPhoneAndEmailValues(): void {
    const phoneContact = this.contacts.find(contact => contact.contact_type === 'TELEPHONE');
    const emailContact = this.contacts.find(contact => contact.contact_type === 'EMAIL');
    
    this.phoneValue = phoneContact ? phoneContact.value : '';
    this.emailValue = emailContact ? emailContact.value : '';
  }
}
