import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'hourlyWage',
  standalone: true
})
export class HourlyWagePipe implements PipeTransform {
  transform(value: number | unknown): string {
    if (typeof value === 'number') {
      return value.toFixed(2);
    } else {
      return 'Invalid hourly wage';
    }
  }
}
