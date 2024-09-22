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
  addons: any[] = [];
  meals: any[] = [];

  sizeOrder = ['SMALL', 'MEDIUM', 'LARGE', 'XL'];


  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.loadBeverages();
    this.loadAddons();
    this.loadMeals();
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

  loadAddons(): void {
    this.menuService.getAddons().subscribe(
      (data: any[]) => {
          this.addons = data;
      },
      (error) => {
        console.log('Error loading addons', error);
      }
    )
  }

  loadMeals(): void {
    this.menuService.getMeals().subscribe(
      (data: any[]) => {
        this.meals = data;
      },
      (error) => {
        console.log('Error loading meals', error);
      }
    )
  }

  formatCapacity(price: number): string {
    return price + ' L';
  }

  formatPrice(price: number | unknown): string {
    if (typeof price === 'number') {
      return price.toFixed(2) + ' zl';
    } else {
      return 'Invalid price';
    }
  }

  sortSizes = (a: any, b: any): number => {
    return this.sizeOrder.indexOf(a.key) - this.sizeOrder.indexOf(b.key);
  };
}
