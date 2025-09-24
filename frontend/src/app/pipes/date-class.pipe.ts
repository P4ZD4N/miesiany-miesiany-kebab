import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateClass',
  standalone: true
})
export class DateClassPipe implements PipeTransform {
  transform(date: string | Date): string {
    if (!date) return '';

    const today = new Date();
    const compareDate = new Date(date);

    today.setHours(0, 0, 0, 0);
    compareDate.setHours(0, 0, 0, 0);

    if (compareDate < today) return 'expired';
    if (compareDate.getTime() === today.getTime()) return 'today';
    return 'future';
  }
}
