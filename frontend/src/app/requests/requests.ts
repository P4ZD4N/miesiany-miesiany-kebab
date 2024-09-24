import { DayOfWeek } from "../enums/day-of-week.enum";

export interface NewBeverageRequest {
  name: string;
  capacity: number;
  price: number;
}
  
export interface UpdatedBeverageRequest {
  name: string;
  new_capacity: number;
  old_capacity: number;
  price: number;
}
  
export interface RemovedBeverageRequest {
  name: string;
  capacity: number;
}

export interface UpdatedHourRequest {
  day_of_week: DayOfWeek
  opening_time: string
  closing_time: string
}
  
export interface AuthenticationRequest {
  email: string
  password: string
}