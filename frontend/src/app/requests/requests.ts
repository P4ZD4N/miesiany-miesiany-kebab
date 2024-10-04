import { DayOfWeek } from "../enums/day-of-week.enum";
import { Size } from "../enums/size.enum";
import { SimpleMealIngredient } from "../responses/responses";

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

export interface UpdatedAddonRequest {
  updated_addon_name: string;
  updated_addon_price: number;
}

export interface RemovedAddonRequest {
  name: string;
}

export interface NewMealRequest {
  new_meal_name: string;
  new_meal_prices: { [key in Size]?: number };
  new_meal_ingredients: SimpleMealIngredient[];
}

export interface UpdatedMealRequest {
  updated_meal_name: string;
  updated_meal_prices: { [key in Size]?: number };
  updated_meal_ingredients: SimpleMealIngredient[];
}

export interface RemovedMealRequest {
  name: string;
}

export interface RemovedIngredientRequest {
  name: string;
}

export interface NewIngredientRequest {
  new_ingredient_name: string;
  new_ingredient_type: number;
}
