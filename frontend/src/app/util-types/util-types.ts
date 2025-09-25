import { EmploymentType } from "../enums/employment-type.enum";
import { RequirementType } from "../enums/requirement-type.enum";
import { Size } from "../enums/size.enum";

export interface BeveragePromotion {
  id: number;
  description: string;
  discount_percentage: number;
}

export interface AddonPromotion {
  id: number;
  description: string;
  discount_percentage: number;
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

export interface JobRequirement {
  requirement_type: RequirementType | null;
  description: string;
}

export interface JobEmploymentType {
  employment_type: EmploymentType  | null;
}

export interface JobApplicationFormData {
  positionName: string;
  firstName: string;
  lastName: string;
  email: string;
  telephone: string;
  additionalMessage: string;
  isStudent: boolean;
  cvFile: File;
}