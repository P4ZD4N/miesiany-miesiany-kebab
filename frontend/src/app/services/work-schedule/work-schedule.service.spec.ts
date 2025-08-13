import { TestBed } from '@angular/core/testing';

import { WorkScheduleService } from './work-schedule.service';

describe('WorkScheduleService', () => {
  let service: WorkScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkScheduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
