import { ContactType } from "../enums/contact-type.enum";
import { DayOfWeek } from "../enums/day-of-week.enum";
import { EmploymentType } from "../enums/employment-type.enum";
import { OrderStatus } from "../enums/order-status.enum";
import { OrderType } from "../enums/order-type.enum";
import { Role } from "../enums/role.enum";
import { Size } from "../enums/size.enum";
import { BeveragePromotion, JobEmploymentType, JobRequirement, MealPromotion, SimpleMealIngredient } from "../util-types/util-types";

export interface CommonResponse {
  status_code: number;
  message: string;
}

export interface NewBeverageResponse extends CommonResponse {}
export interface UpdatedBeverageResponse extends CommonResponse {}
export interface RemovedBeverageResponse extends CommonResponse {}

export interface BeverageResponse {
  name: string;
  capacity: number;
  price: number;
  promotion: BeveragePromotion;
  isEditing?: boolean;
}

export interface AddonResponse {
  name: string;
  price: number;
  promotion: AddonPromotionResponse;
  isEditing?: boolean;
}

export interface MealResponse {
  name: string;
  prices: { [key in Size]: number };
  ingredients: SimpleMealIngredient[];
  meal_promotions: MealPromotion[];
  isEditing?: boolean;
}

export interface OpeningHoursResponse {
  day_of_week: DayOfWeek
  opening_time: string
  closing_time: string
  isEditing?: boolean;
}

export interface AuthenticationResponse extends CommonResponse {
  role: Role;
}

export interface NewAddonResponse extends CommonResponse {}
export interface UpdatedAddonResponse extends CommonResponse {}
export interface RemovedAddonResponse extends CommonResponse {}
export interface NewMealResponse extends CommonResponse {}

export interface IngredientResponse {
  name: string;
  ingredient_type: string;
}

export interface RemovedMealResponse extends CommonResponse {}
export interface UpdatedMealResponse extends CommonResponse {}
export interface RemovedIngredientResponse extends CommonResponse {}
export interface NewIngredientResponse extends CommonResponse {}

export interface ContactResponse {
  contact_type: ContactType;
  value: string;
}

export interface UpdatedContactResponse extends CommonResponse {}

export interface JobApplicationResponse {
  id: number;
  applicant_first_name: string;
  applicant_last_name: string;
  applicant_email: string;
  applicant_telephone: string;
  additional_message: string;
  is_student: boolean;
  id_cv: number;
}

export interface JobOfferGeneralResponse {
  position_name: string;
  description: string;
  hourly_wage: number;
  is_active: boolean;
  job_employment_types: JobEmploymentType[];
  job_requirements: JobRequirement[];
}

export interface JobOfferManagerResponse {
  position_name: string;
  description: string;
  hourly_wage: number;
  is_active: boolean;
  job_employment_types: JobEmploymentType[];
  job_requirements: JobRequirement[];
  job_applications: JobApplicationResponse[];
}

export interface NewJobOfferResponse extends CommonResponse {}
export interface UpdatedJobOfferResponse extends CommonResponse {}
export interface RemovedJobOfferResponse extends CommonResponse {}

export interface JobOfferApplicationResponse extends CommonResponse {
  application_id: number;
}

export interface NewCvResponse extends CommonResponse {}
export interface RemovedApplicationResponse extends CommonResponse {}

export interface MealPromotionResponse {
  id: number;
  description: string;
  sizes: Size[];
  discount_percentage: number;
  meal_names: string[]
  isEditing?: boolean;
}

export interface BeveragePromotionResponse {
  id: number;
  description: string;
  discount_percentage: number;
  beverages_with_capacities: { [key in string]: number[] };
  isEditing?: boolean;
}

export interface AddonPromotionResponse {
  id: number;
  description: string;
  discount_percentage: number;
  addon_names: string[]
  isEditing?: boolean;
}

export interface NewMealPromotionResponse extends CommonResponse {}
export interface UpdatedMealPromotionResponse extends CommonResponse {}
export interface RemovedMealPromotionResponse extends CommonResponse {}
export interface NewBeveragePromotionResponse extends CommonResponse {}
export interface UpdatedBeveragePromotionResponse extends CommonResponse {}
export interface RemovedBeveragePromotionResponse extends CommonResponse {}
export interface NewAddonPromotionResponse extends CommonResponse {}
export interface UpdatedAddonPromotionResponse extends CommonResponse {}
export interface RemovedAddonPromotionResponse extends CommonResponse {}
export interface NewNewsletterSubscriberResponse extends CommonResponse {}
export interface VerifyNewsletterSubscriptionResponse extends CommonResponse {}
export interface RegenerateOtpResponse extends CommonResponse {}
export interface UnsubscribeResponse extends CommonResponse {}

export interface OrderResponse {
  id: number;
  order_type: OrderType | null;
  order_status: OrderStatus | null;
  customer_phone: string; 
  customer_email: string;
  street: string;
  house_number: number;
  postal_code: string;
  city: string;
  additional_comments: string;
  total_price: number;
  created_at: string;
  meals: { 
    meal_name: string;
    final_price: number;
    size: Size | null;
    quantity: number;
    ingredient_names: string[]
  }[];
  beverages: { 
    beverage_name: string;
    final_price: number;
    capacity: number;
    quantity: number;
  }[];
  addons: { 
    addon_name: string;
    final_price: number;
    quantity: number; 
  }[];
}

export interface NewOrderResponse extends CommonResponse {
  id: number;
}

export interface UpdatedOrderResponse extends CommonResponse {}
export interface RemovedOrderResponse extends CommonResponse {}


export interface DiscountCodeResponse {
  id: number;
  code: string;
  discount_percentage: number;
  expiration_date: Date;
  remaining_uses: number;
}

export interface NewDiscountCodeResponse extends CommonResponse {}
export interface UpdatedDiscountCodeResponse extends CommonResponse {}
export interface RemovedDiscountCodeResponse extends CommonResponse {}

export interface EmployeeResponse {
  id: number;
  first_name: string;
  last_name: string;
  email: string;
  phone_number: string;
  date_of_birth: Date;
  job: string;
  employment_type: EmploymentType,
  hourly_wage: number;
  is_active: boolean;
  is_student: boolean;
  hired_at: Date;
}

export interface WorkScheduleEntryResponse {
  id: number;
  employee_first_name: string;
  employee_last_name: string;
  employee_email: string;
  date: string;
  start_time: string;
  end_time: string;
  hourly_wage: number;
}

export interface NewWorkScheduleEntryResponse extends CommonResponse {}
export interface RemovedWorkScheduleEntryResponse extends CommonResponse {}
export interface NewEmployeeResponse extends CommonResponse {}
export interface UpdatedEmployeeResponse extends CommonResponse {}
export interface RemovedEmployeeResponse extends CommonResponse {}
export interface UpdatedCredentialsResponse extends CommonResponse {}

export interface SessionCheckResponse {
  authenticated: boolean;
  email: string;
  role: Role;
}