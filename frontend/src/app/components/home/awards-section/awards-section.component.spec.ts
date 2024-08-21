import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AwardsSectionComponent } from './awards-section.component';

describe('AwardsSectionComponent', () => {
  let component: AwardsSectionComponent;
  let fixture: ComponentFixture<AwardsSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AwardsSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AwardsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
