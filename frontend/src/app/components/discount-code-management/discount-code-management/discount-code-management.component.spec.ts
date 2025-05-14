import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscountCodeManagementComponent } from './discount-code-management.component';

describe('DiscountCodeManagementComponent', () => {
  let component: DiscountCodeManagementComponent;
  let fixture: ComponentFixture<DiscountCodeManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscountCodeManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiscountCodeManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
