import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'price',
  standalone: true
})
export class PricePipe implements PipeTransform {
  transform(price: number): string {
    if (typeof price === 'number') return price.toFixed(2) + ' zl';
    else return 'Invalid price';
  }
}
