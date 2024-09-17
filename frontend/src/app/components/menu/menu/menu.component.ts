import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../services/menu/menu.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  beverages: any[] = [];


  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.loadBeverages();
  }

  loadBeverages(): void {
    this.menuService.getBeverages().subscribe(
      (data: any[]) => {
          this.beverages = data;
      },
      (error) => {
        console.log('Error loading beverages', error);
      }
    )
  }

  formatCapacity(price: number): string {
    return price + ' L';
  }

  formatPrice(price: number): string {
    return price.toFixed(2) + ' zl';
  }
}
