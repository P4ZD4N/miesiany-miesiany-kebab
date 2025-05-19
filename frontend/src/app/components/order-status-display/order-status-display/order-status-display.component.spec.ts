import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderStatusDisplayComponent } from './order-status-display.component';

describe('OrderStatusDisplayComponent', () => {
  let component: OrderStatusDisplayComponent;
  let fixture: ComponentFixture<OrderStatusDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderStatusDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderStatusDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
