import { ContactType } from "../enums/contact-type.enum";
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
  isEditing?: boolean;
}

export interface SimpleMealIngredient {
  id?: number;
  name: string;
  ingredient_type: string;
}

export interface MealResponse {
  name: string;
  prices: { [key in Size]: number };
  ingredients: SimpleMealIngredient[];
  isEditing?: boolean;
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

export interface NewAddonResponse {
  status_code: number;
  message: string;
}
  
export interface UpdatedAddonResponse {
  status_code: number;
  message: string;
}

export interface RemovedAddonResponse {
  status_code: number;
  message: string;
}  

export interface NewMealResponse {
  status_code: number;
  message: string;
}

export interface IngredientResponse {
  name: string;
  ingredient_type: string;
}

export interface RemovedMealResponse {
  status_code: number;
  message: string;
}

export interface UpdatedMealResponse {
  status_code: number;
  message: string;
}

export interface RemovedIngredientResponse {
  status_code: number;
  message: string;
}

export interface NewIngredientResponse {
  status_code: number;
  message: string;
}

export interface ContactResponse {
  contact_type: ContactType;
  value: string;
}

export interface UpdatedContactResponse {
  status_code: number;
  message: string;
}