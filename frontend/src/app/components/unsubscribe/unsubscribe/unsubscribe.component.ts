import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { UnsubscribeRequest } from '../../../requests/requests';
import { NewsletterService } from '../../../services/newsletter/newsletter.service';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-unsubscribe',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './unsubscribe.component.html',
  styleUrl: './unsubscribe.component.scss',
})
export class UnsubscribeComponent implements OnInit {
  unsubscribeRequest: UnsubscribeRequest = {
    email: '',
  };

  constructor(
    private route: ActivatedRoute,
    private newsletterService: NewsletterService,
    private router: Router,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.unsubscribeRequest = {
        email: params['email'] || '',
      };
    });
  }

  unsubscribe(): void {
    this.alertService.showUnsubscribeAlert().then((confirmed) => {
      if (!confirmed) return;

      this.newsletterService
        .unsubscribe(this.unsubscribeRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulUnsubscribeAlert();
          this.router.navigate(['/']);
        });
    });
  }
}
