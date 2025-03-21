import { ContactType } from "../enums/contact-type.enum";
import { DayOfWeek } from "../enums/day-of-week.enum";
import { IngredientType } from "../enums/ingredient-type.enum";
import { NewsletterMessagesLanguage } from "../enums/newsletter-messages-language.enum";
import { Size } from "../enums/size.enum";
import { JobEmploymentType, JobRequirement, SimpleMealIngredient } from "../responses/responses";

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
  new_ingredient_type: IngredientType | null;
}

export interface UpdatedContactRequest {
  contact_type: ContactType;
  new_value: string;
}

export interface NewJobOfferRequest {
  position_name: string;
  description: string;
  hourly_wage: number;
  job_employment_types: JobEmploymentType[];
  job_requirements: JobRequirement[];
}

export interface UpdatedJobOfferRequest {
  position_name: string;
  updated_position_name?: string;
  updated_description?: string;
  updated_hourly_wage?: number;
  updated_employment_types?: JobEmploymentType[];
  updated_requirements?: JobRequirement[];
  is_active?: boolean;
}

export interface RemovedJobOfferRequest {
  position_name: string;
}

export interface JobOfferApplicationRequest {
  position_name: string;
  applicant_first_name: string;
  applicant_last_name: string; 
  applicant_email: string; 
  applicant_telephone: string;
  additional_message?: string;
  is_student: boolean;
}

export interface RemovedApplicationRequest {
  position_name: string;
  application_id: number;
}

export interface NewMealPromotionRequest {
  description: string;
  sizes: Size[];
  discount_percentage: number;
  meal_names: string[]
}

export interface UpdatedMealPromotionRequest {
  id: number;
  updated_description: string;
  updated_sizes: Size[];
  updated_discount_percentage: number;
  updated_meal_names: string[];
}

export interface RemovedMealPromotionRequest {
  id: number;
}

export interface NewBeveragePromotionRequest {
  description: string;
  discount_percentage: number;
  beverages_with_capacities: { [key in string]: number[] };
}

export interface UpdatedBeveragePromotionRequest {
  id: number;
  updated_description: string;
  updated_discount_percentage: number;
  updated_beverages_with_capacities: { [key in string]: number[] };
}

export interface RemovedBeveragePromotionRequest {
  id: number;
}

export interface NewAddonPromotionRequest {
  description: string;
  discount_percentage: number;
  addon_names: string[]
}

export interface UpdatedAddonPromotionRequest {
  id: number;
  updated_description: string;
  updated_discount_percentage: number;
  updated_addon_names: string[];
}

export interface RemovedAddonPromotionRequest {
  id: number;
}

export interface NewNewsletterSubscriberRequest {
  first_name: string;
  email: string;
  messages_language: NewsletterMessagesLanguage;
}

export interface VerifyNewsletterSubscriptionRequest {
  email: string;
  otp: number;
}

export interface RegenerateOtpRequest {
  email: string;
}

export interface UnsubscribeRequest {
  email: string;
}