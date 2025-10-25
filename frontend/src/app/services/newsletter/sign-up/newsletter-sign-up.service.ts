import { Injectable } from '@angular/core';
import {
  NewNewsletterSubscriberRequest,
  VerifyNewsletterSubscriptionRequest,
  RegenerateOtpRequest,
} from '../../../requests/requests';
import { LangService } from '../../lang/lang.service';
import { NewsletterService } from '../newsletter.service';
import { AlertService } from '../../alert/alert.service';

@Injectable({
  providedIn: 'root',
})
export class NewsletterSignUpService {
  errorMessages: { [key: string]: string } = {};

  constructor(
    private langService: LangService,
    private newsletterService: NewsletterService,
    private alertService: AlertService
  ) {}

  async startSigningUpToNewsletter(): Promise<void> {
    const firstName = await this.alertService.getNewsletterFirstNameAlert();
    if (firstName)
      this.startEnteringNewsletterEmail(
        firstName.charAt(0).toUpperCase() +
          firstName.slice(1).toLocaleLowerCase()
      );
  }

  private async startEnteringNewsletterEmail(firstName: string): Promise<void> {
    const email = await this.alertService.getNewsletterEmailAlert(firstName);
    this.startChoosingNewsletterLanguage(firstName, email);
  }

  private async startChoosingNewsletterLanguage(
    firstName: string,
    email: string
  ): Promise<void> {
    const language = await this.alertService.getNewsletterLanguageAlert(
      firstName
    );

    if (language)
      this.subscribeNewsletter({
        first_name: firstName,
        email: email,
        messages_language: language,
      });
  }

  private async subscribeNewsletter(
    request: NewNewsletterSubscriberRequest
  ): Promise<void> {
    this.newsletterService.subscribe(request).subscribe({
      next: async () => {
        const title =
          this.langService.currentLang === 'pl'
            ? 'Potwierdz subskrypcje!'
            : 'Confirm subscription!';
        const text =
          this.langService.currentLang === 'pl'
            ? 'Wyslalismy do Ciebie wiadomosc email, w ktorej znajdziesz kod. Wpisz go ponizej'
            : 'We sent to you email message, in which you can find code. Enter it below';

        this.getAndProcessOtp(title, text, request);
      },
      error: async (error) => {
        this.handleError(error);
        this.alertService.showSubscribeNewsletterErrorAlert(error);
      },
    });
  }

  private verifySubscription(request: VerifyNewsletterSubscriptionRequest) {
    this.newsletterService.verifySubscription(request).subscribe({
      next: async () =>
        this.alertService.showSuccessfulNewsletterSubscribeAlert(),
      error: async (error) => {
        const title =
          this.langService.currentLang === 'pl'
            ? 'Sprobuj ponownie!'
            : 'Try again!';
        const text =
          error.errorMessages?.message ||
          (this.langService.currentLang === 'pl'
            ? 'Wystapil blad. Sprawdz kod i sprobuj ponownie.'
            : 'A validation error occurred. Check code and try again.');

        this.getAndProcessOtp(title, text, request);
      },
    });
  }

  private regenerateOtp(request: RegenerateOtpRequest) {
    this.newsletterService.regenerateOtp(request).subscribe({
      next: async () => {
        const title =
          this.langService.currentLang === 'pl'
            ? 'Nowy kod wygenerowany!'
            : 'New code generated!';
        const text =
          this.langService.currentLang === 'pl'
            ? 'Wyslalismy do Ciebie wiadomosc email, w ktorej znajdziesz nowy kod. Wpisz go ponizej'
            : 'We sent to you email message, in which you can find new code. Enter it below';

        this.getAndProcessOtp(title, text, request);
      },
      error: async (error) => {
        const title =
          this.langService.currentLang === 'pl'
            ? 'Sprobuj ponownie!'
            : 'Try again!';
        const text =
          error.errorMessages?.message ||
          (this.langService.currentLang === 'pl'
            ? 'Wystapil blad. Sprawdz kod i sprobuj ponownie.'
            : 'A validation error occurred. Check code and try again.');
        this.getAndProcessOtp(title, text, request);
      },
    });
  }

  private async getAndProcessOtp(
    alertTitle: string,
    alertText: string,
    request:
      | VerifyNewsletterSubscriptionRequest
      | NewNewsletterSubscriberRequest
      | RegenerateOtpRequest
  ): Promise<void> {
    const otp = await this.alertService.getNewsletterOtpAlert(
      alertTitle,
      alertText
    );

    if (otp.isConfirmed && otp.value) {
      this.verifySubscription({
        otp: Number(otp.value),
        email: request.email,
      });
    } else if (otp.isDenied) {
      this.regenerateOtp({
        email: request.email,
      });
    }
  }

  private handleError(error: any) {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }
}
