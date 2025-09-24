import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneFormat',
  standalone: true,
})
export class PhoneFormatPipe implements PipeTransform {
  transform(telephone: string): string {
    if (!telephone) return '';

    const cleaned = telephone.replace(/\D/g, ''); 
    if (cleaned.length !== 9) return telephone;

    return `+48 ${cleaned.slice(0, 3)} ${cleaned.slice(3, 6)} ${cleaned.slice(6, 9)}`;
  }
}
