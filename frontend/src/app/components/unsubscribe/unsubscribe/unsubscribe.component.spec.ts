import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnsubscribeComponent } from '../unsubscribe/unsubscribe.component';

describe('UnsubscribeComponent', () => {
  let component: UnsubscribeComponent;
  let fixture: ComponentFixture<UnsubscribeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnsubscribeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnsubscribeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
