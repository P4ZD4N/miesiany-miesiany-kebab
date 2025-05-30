import { ContactType } from "../enums/contact-type.enum";
import { DayOfWeek } from "../enums/day-of-week.enum";
import { EmploymentType } from "../enums/employment-type.enum";
import { OrderStatus } from "../enums/order-status.enum";
import { OrderType } from "../enums/order-type.enum";
import { RequirementType } from "../enums/requirement-type.enum";
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

export interface BeveragePromotion {
  id: number;
  description: string;
  discount_percentage: number;
}

export interface BeverageResponse {
  name: string;
  capacity: number;
  price: number;
  promotion: BeveragePromotion;
  isEditing?: boolean;
}

export interface AddonPromotion {
  id: number;
  description: string;
  discount_percentage: number;
}

export interface AddonResponse {
  name: string;
  price: number;
  promotion: AddonPromotionResponse;
  isEditing?: boolean;
}

export interface SimpleMealIngredient {
  id?: number;
  name: string;
  ingredient_type: string;
}

export interface MealPromotion {
  id: number;
  description: string;
  sizes: Size[];
  discount_percentage: number;
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

export interface JobRequirement {
  requirement_type: RequirementType | null;
  description: string;
}

export interface JobEmploymentType {
  employment_type: EmploymentType  | null;
}

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

export interface NewJobOfferResponse {
  status_code: number;
  message: string;
}

export interface UpdatedJobOfferResponse {
  status_code: number;
  message: string;
}

export interface RemovedJobOfferResponse {
  status_code: number;
  message: string;
}

export interface JobOfferApplicationResponse {
  application_id: number;
  status_code: number;
  message: string;
}

export interface NewCvResponse {
  status_code: number;
  message: string;
}

export interface RemovedApplicationResponse {
  status_code: number;
  message: string;
}

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

export interface NewMealPromotionResponse {
  status_code: number;
  message: string;
}

export interface UpdatedMealPromotionResponse {
  status_code: number;
  message: string;
}

export interface RemovedMealPromotionResponse {
  status_code: number;
  message: string;
}

export interface NewBeveragePromotionResponse {
  status_code: number;
  message: string;
}

export interface UpdatedBeveragePromotionResponse {
  status_code: number;
  message: string;
}

export interface RemovedBeveragePromotionResponse {
  status_code: number;
  message: string;
}

export interface NewAddonPromotionResponse {
  status_code: number;
  message: string;
}

export interface UpdatedAddonPromotionResponse {
  status_code: number;
  message: string;
}

export interface RemovedAddonPromotionResponse {
  status_code: number;
  message: string;
}

export interface NewNewsletterSubscriberResponse {
  status_code: number;
  message: string;
}

export interface VerifyNewsletterSubscriptionResponse {
  status_code: number;
  message: string;
}

export interface RegenerateOtpResponse {
  status_code: number;
  message: string;
}

export interface UnsubscribeResponse {
  status_code: number;
  message: string;
}

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

export interface NewOrderResponse {
  status_code: number;
  message: string;
  id: number;
}

export interface UpdatedOrderResponse {
  status_code: number;
  message: string;
}

export interface RemovedOrderResponse {
  status_code: number;
  message: string;
}


export interface DiscountCodeResponse {
  id: number;
  code: string;
  discount_percentage: number;
  expiration_date: Date;
  remaining_uses: number;
}

export interface NewDiscountCodeResponse {
  status_code: number;
  message: string;
}

export interface UpdatedDiscountCodeResponse {
  status_code: number;
  message: string;
}

export interface RemovedDiscountCodeResponse {
  status_code: number;
  message: string;
}