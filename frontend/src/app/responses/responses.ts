import { DayOfWeek } from "../enums/day-of-week.enum";
import { Role } from "../enums/role.enum";
import { Size } from "../enums/size.enum";

export interface NewBeverageResponse {
  status_code: number;
  message: string;
}

export interface UpdatedBeverageResponse {
  status_code: number;
  message: string;
}

export interface RemovedBeverageResponse {
  status_code: number;
  message: string;
}

export interface BeverageResponse {
  name: string;
  capacity: number;
  price: number;
  isEditing?: boolean;
}

export interface AddonResponse {
  name: string;
  price: number;
}

export interface SimpleMealIngredient {
  id: number;
  name: string;
  ingredient_type: string;
}

export interface MealResponse {
  name: string;
  prices: { [key in Size]: number };
  ingredients: SimpleMealIngredient[];
}

export interface OpeningHoursResponse {
  day_of_week: DayOfWeek
  opening_time: string
  closing_time: string
  isEditing?: boolean;
}

export interface AuthenticationResponse {
  status_code: number
  message: string
  role: Role;
}
  
  