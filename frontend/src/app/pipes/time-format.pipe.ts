import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeFormat',
  standalone: true
})
export class TimeFormatPipe implements PipeTransform {
  transform(time: string): string {
    const hours = time.slice(0, 2);
    const minutes = time.slice(3, 5);
    return `${hours}:${minutes}`;
  }
}
