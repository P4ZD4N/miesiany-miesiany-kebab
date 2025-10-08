import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'capacity',
  standalone: true
})
export class CapacityPipe implements PipeTransform {
  transform(price: number): string {
    return price + ' L';
  }
}
