import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import tt from '@tomtom-international/web-sdk-maps';
import { environment } from '../../../../environments/environment.development';

@Component({
  selector: 'app-location-section',
  standalone: true,
  imports: [TranslateModule],
  templateUrl: './location-section.component.html',
  styleUrl: './location-section.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class LocationSectionComponent implements OnInit {
  kebabCoordinates: [number, number] = [18.978145609677117, 50.73271996898827];

  ngOnInit() {
    const map = tt.map({
      key: environment.MAP_API_KEY,
      container: 'map',
      center: this.kebabCoordinates,
      zoom: 15,
    });

    map.addControl(new tt.NavigationControl());

    new tt.Marker().setLngLat(this.kebabCoordinates).addTo(map);
  }
}
