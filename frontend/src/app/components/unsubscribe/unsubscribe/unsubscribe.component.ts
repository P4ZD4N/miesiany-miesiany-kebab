import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { UnsubscribeRequest } from '../../../requests/requests';
import Swal from 'sweetalert2';
import { LangService } from '../../../services/lang/lang.service';
import { NewsletterService } from '../../../services/newsletter/newsletter.service';

@Component({
  selector: 'app-unsubscribe',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './unsubscribe.component.html',
  styleUrl: './unsubscribe.component.scss'
})
export class UnsubscribeComponent implements OnInit {

  unsubscribeRequest: UnsubscribeRequest = {
    email: ''
  };

  constructor(
    private route: ActivatedRoute, 
    private langService: LangService, 
    private newsletterService: NewsletterService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.unsubscribeRequest = {
        email: params['email'] || ''
      }
    });
  }

  unsubscribe(): void {
    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz odsubskrybowac?`
        : `Are you sure you want to sign out?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.newsletterService.unsubscribe(this.unsubscribeRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie odsubskrybowano!` : `Successfully signed out!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });

          this.router.navigate(['/']);
        });
      }
    });
  }
}
