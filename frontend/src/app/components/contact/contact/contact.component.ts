import { Component, OnInit } from '@angular/core';
import tt from '@tomtom-international/web-sdk-maps';
import { environment } from '../../../../environments/environment.development';
import { ContactResponse } from '../../../responses/responses';
import { ContactService } from '../../../services/contact/contact.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { UpdatedContactRequest } from '../../../requests/requests';
import { ContactType } from '../../../enums/contact-type.enum';
import { LangService } from '../../../services/lang/lang.service';
import { AlertService } from '../../../services/alert/alert.service';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { PhoneFormatPipe } from '../../../pipes/phone-format.pipe';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [FormsModule, TranslateModule, CommonModule, PhoneFormatPipe],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
})
export class ContactComponent implements OnInit {
  contacts: ContactResponse[] = [];
  errorMessages: { [key: string]: string } = {};
  kebabCoordinates: [number, number] = [18.978145609677117, 50.73271996898827];

  isEditingPhone: boolean = false;
  isEditingEmail: boolean = false;
  isEditing: boolean = false;

  phoneValue: string = '';
  emailValue: string = '';

  languageChangeSubscription: Subscription;

  constructor(
    private contactService: ContactService,
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private translationHelper: TranslationHelperService,
    private alertService: AlertService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    const map = tt.map({
      key: environment.MAP_API_KEY,
      container: 'map',
      center: this.kebabCoordinates,
      zoom: 15,
    });

    map.addControl(new tt.NavigationControl());

    new tt.Marker().setLngLat(this.kebabCoordinates).addTo(map);

    this.loadContacts();
  }

  private loadContacts(): void {
    this.contactService.getContacts().subscribe({
      next: (data: ContactResponse[]) => {
        this.contacts = data;
        this.initPhoneAndEmailValues();
      },
      error: (error) => console.log('Error loading contacts', error),
    });
  }

  private initPhoneAndEmailValues(): void {
    const phoneContact = this.contacts.find(
      (contact) => contact.contact_type === 'TELEPHONE'
    );
    const emailContact = this.contacts.find(
      (contact) => contact.contact_type === 'EMAIL'
    );

    this.phoneValue = phoneContact?.value ?? '';
    this.emailValue = emailContact?.value ?? '';
  }

  protected updatePhone(): void {
    let contactTypeTranslated: string =
      this.translationHelper.getTranslatedContactType(ContactType.TELEPHONE);
    let updatedPhone: UpdatedContactRequest = {
      contact_type: ContactType.TELEPHONE,
      new_value: this.phoneValue,
    };

    this.contactService.updateContact(updatedPhone).subscribe({
      next: () => {
        this.alertService.showSuccessfulContactUpdateAlert(
          contactTypeTranslated
        );
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

  protected updateEmail(): void {
    let contactTypeTranslated: string =
      this.translationHelper.getTranslatedContactType(ContactType.EMAIL);
    let updatedEmail: UpdatedContactRequest = {
      contact_type: ContactType.EMAIL,
      new_value: this.emailValue,
    };

    this.contactService.updateContact(updatedEmail).subscribe({
      next: () => {
        this.alertService.showSuccessfulContactUpdateAlert(
          contactTypeTranslated
        );
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

  protected toggleEditPhone(): void {
    this.isEditing = true;
    this.isEditingPhone = !this.isEditingPhone;
  }

  protected toggleEditEmail(): void {
    this.isEditing = true;
    this.isEditingEmail = !this.isEditingEmail;
  }

  protected hideEditPhoneForm(): void {
    this.hideErrorMessages();
    this.isEditingPhone = false;
    this.isEditing = false;
    this.initPhoneAndEmailValues();
  }

  protected hideEditEmailForm(): void {
    this.hideErrorMessages();
    this.isEditingEmail = false;
    this.isEditing = false;
    this.initPhoneAndEmailValues();
  }

  protected getTelephone(): ContactResponse[] {
    return this.contacts.filter(
      (contact) => contact.contact_type === 'TELEPHONE'
    );
  }

  protected getEmail(): ContactResponse[] {
    return this.contacts.filter((contact) => contact.contact_type === 'EMAIL');
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  private handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }

  private hideErrorMessages(): void {
    this.errorMessages = {};
  }
}
