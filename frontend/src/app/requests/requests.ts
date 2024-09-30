import { DayOfWeek } from "../enums/day-of-week.enum";

export interface NewBeverageRequest {
  new_beverage_name: string;
  new_beverage_capacity: number;
  new_beverage_price: number;
}
  
export interface UpdatedBeverageRequest {
  updated_beverage_name: string;
  updated_beverage_new_capacity: number;
  updated_beverage_old_capacity: number;
  updated_beverage_price: number;
}
  
export interface RemovedBeverageRequest {
  name: string;
  capacity: number;
}

export interface UpdatedHourRequest {
  day_of_week: DayOfWeek;
  opening_time: string;
  closing_time: string;
}
  
export interface AuthenticationRequest {
  email: string;
  password: string;
}

export interface NewAddonRequest {
  new_addon_name: string;
  new_addon_price: number;
}
