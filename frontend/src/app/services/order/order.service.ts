import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LangService } from '../lang/lang.service';
import { AuthenticationService } from '../authentication/authentication.service';
import { MenuService } from '../menu/menu.service';
import { AddonResponse, BeverageResponse, IngredientResponse, MealPromotion, MealResponse } from '../../responses/responses';
import Swal from 'sweetalert2';
import { Size } from '../../enums/size.enum';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  beverages: BeverageResponse[] = [];
  addons: AddonResponse[] = [];
  meals: MealResponse[] = [];
  ingredients: IngredientResponse[] = [];

  constructor(
    private translate: TranslateService,
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private menuService: MenuService
  ) { }

  loadBeverages(): void {
      this.menuService.getBeverages().subscribe(
        (data: BeverageResponse[]) => {
          this.beverages = data;
        },
        (error) => {
          console.log('Error loading beverages', error);
        }
      );
    }
  
    loadAddons(): void {
      this.menuService.getAddons().subscribe(
        (data: AddonResponse[]) => {
          this.addons = data;
        },
        (error) => {
          console.log('Error loading addons', error);
        }
      );
    }
  
    loadMeals(): void {
      this.menuService.getMeals().subscribe(
        (data: MealResponse[]) => {
          this.meals = data;
        },
        (error) => {
          console.log('Error loading meals', error);
        }
      );
    }
  
    loadIngredients(): void {
      this.menuService.getIngredients().subscribe(
        (data: IngredientResponse[]) => {
          this.ingredients = data;
        },
        (error) => {
          console.log('Error loading ingredients', error);
        }
      );
    }

  async startPlacingOrder(): Promise<void> {
  
    this.loadMeals();
    this.loadIngredients();
    this.loadBeverages();
    this.loadAddons();

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac co chcesz zamowic' : 'Please select what do you want to order';

    const inputOptions = new Promise((resolve) => {
      resolve({
        "MEAL": this.langService.currentLang === 'pl' ? `🍽️ Dania` : `🍽️ Meals`,
        "BEVERAGE": this.langService.currentLang === 'pl' ? `🥤 Napoje` : `🥤 Beverages`,
        "ADDON": this.langService.currentLang === 'pl' ? `🧂 Dodatki` : `🧂 Addons`
      });
    });

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' ? `Co chcesz zamowic?` : `What do you want to order?`,
      input: "radio",
      inputOptions,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });
    
    if (choice) {
      if (choice == "MEAL") this.startChoosingMeal();
      if (choice == "BEVERAGE") this.startChoosingBeverage();
      if (choice == "ADDON") this.startChoosingAddonProperties();
    }
  }

  async startChoosingMeal(): Promise<void> {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac danie' : 'Please select meal';

    const mealNamesOriginal = this.meals.map(meal => meal.name).sort();

    const mealNamesTranslated = this.meals.map(meal => {
      let mealNameTranslated = this.translate.instant('menu.meals.' + meal.name);
      if (!this.isMealTranslationAvailable(meal.name)) mealNameTranslated = meal.name;
      return mealNameTranslated;
    }).sort();

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' ? `Wybierz interesujace Cie danie` : `Choose interesting meal`,
      input: "radio",
      inputOptions: mealNamesTranslated,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });
    
    if (choice) {
      this.startChoosingMealProperties(mealNamesOriginal[choice])
    }
  }

  async startChoosingMealProperties(mealName: string): Promise<void> {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac danie' : 'Please select meal';

    let mealNameTranslated = this.translate.instant('menu.meals.' + mealName);

    if (!this.isMealTranslationAvailable(mealName)) {
      mealNameTranslated = mealName;
    }

    const ingredientIcons: { [key: string]: string } = {
      BREAD: 'fa-solid fa-bread-slice',
      MEAT: 'fa-solid fa-drumstick-bite',
      VEGETABLE: 'fa-solid fa-carrot',
      SAUCE: 'fas fa-wine-bottle',
      OTHER: 'fa-solid fa-plus'
    };

    const meal = this.meals.filter(meal => meal.name === mealName)[0];
    
    const mealIngredients = meal.ingredients.map(ingredient => {
        let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);
        
        if (!this.isIngredientTranslationAvailable(ingredient.name)) {
          ingredientNameTranslated = ingredient.name;
        }
    
        const icon = ingredientIcons[ingredient.ingredient_type] || 'fa-solid fa-question';
    
        return `<li style="display: flex; align-items: center; justify-content: center; gap: 10px;">
                  <i class="${icon}" style="color: red;"></i> ${ingredientNameTranslated}
                </li>`;
      }).join('');

    const mealSizes = Object.entries(this.meals[0].prices).map(([size, price]) => {
        let sizeTranslated = this.translate.instant('menu.meals.sizes.' + size);
        return `<option value="${size}" data-price="${price}">${sizeTranslated}</option>`;
      }).join('');

    const meats = this.ingredients
      .filter(ingredient => ingredient.ingredient_type === 'MEAT')
      .map(ingredient => {
        let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

        if (!this.isIngredientTranslationAvailable(ingredient.name)) {
          ingredientNameTranslated = ingredient.name;
        }

        return `<option value="${ingredient.name}">${ingredientNameTranslated}</option>`;
      }).join('');

    const sauces = this.ingredients
    .filter(ingredient => ingredient.ingredient_type === 'SAUCE')
    .map(ingredient => {
      let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

      if (!this.isIngredientTranslationAvailable(ingredient.name)) {
        ingredientNameTranslated = ingredient.name;
      }

      return `<option value="${ingredient.name}">${ingredientNameTranslated}</option>`;
    }).join('');

    const ingredientsHeading = this.langService.currentLang === 'pl' ? `<h3>Skladniki</h3>` : `<h3>Ingredients</h3>`;
    const selectSizeHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz rozmiar</h3>` : `<h3>Select size</h3>`;
    const selectMeatHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz mieso</h3>` : `<h3>Select meat</h3>`;
    const selectSauceHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz sos</h3>` : `<h3>Select sauce</h3>`;
    const howManyHeading = this.langService.currentLang === 'pl' ? `<h3>Jaka ilosc?</h3>` : `<h3>How many?</h3>`;

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: `<span style="color: red;">${mealNameTranslated}</span>`,
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj do zamowienia' : 'Add to order',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      html: `
        <style>
          input[type="number"], select {
            color: white;
            text-align: center;
            background-color: #141414;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
            box-shadow: none !important;
          }

          select:focus, input[type="number"]:focus {
            border: 1px solid red !important; 
          }
        </style>

        <div style="margin-bottom: 20px;">
          ${ingredientsHeading}
          <ul style="list-style: none; padding: 0;">
            ${mealIngredients}
          </ul>
        </div>

        <div style="margin-bottom: 20px;">
          ${selectSizeHeading}
          <select id="sizeSelection" class="swal2-input">
            ${mealSizes}
          </select>
        </div>
        
        <div style="margin-bottom: 20px;">
          ${selectMeatHeading}
          <select id="meatSelection" class="swal2-input">
            ${meats}
          </select>
        </div>

        <div style="margin-bottom: 20px;">
          ${selectSauceHeading}
          <select id="sauceSelection" class="swal2-input">
            ${sauces}
          </select>
        </div>

        <div style="margin-bottom: 20px;">
          ${howManyHeading}
          <input id="kebabQuantity" class="swal2-input" type="number" value="1" min="1" max=10 style="width: 50%; text-align: center;">
        </div>

        <div style="margin-bottom: 20px;">
          <h4>
            <span id="mealPrice">${meal.prices['SMALL']}</span>
          </h4>
        </div>
      `,
      didOpen: () => {
        const updatePrice = () => {
          const sizeSelect = document.getElementById('sizeSelection') as HTMLSelectElement;
          const quantityInput = document.getElementById('kebabQuantity') as HTMLInputElement;
          
          const selectedSize = sizeSelect.value as Size;
          const baseUnitPrice = meal.prices[selectedSize];
          const quantity = parseInt(quantityInput.value) || 1;
          
          let finalUnitPrice = baseUnitPrice;
          let activePromotion: MealPromotion | null = null;

          meal.meal_promotions.forEach(promotion => {
            if (promotion.sizes.includes(selectedSize)) {
                finalUnitPrice = baseUnitPrice * (1 - promotion.discount_percentage / 100);
                activePromotion = promotion;
            }
          });

          const totalPrice = (finalUnitPrice * quantity).toFixed(2);
          const priceDisplay = document.getElementById('mealPrice');
          
          if (priceDisplay) {
            if (activePromotion) {
              priceDisplay.innerHTML = `
                  <span style="text-decoration: line-through; color: red; margin-right: 10px;">
                      ${(baseUnitPrice * quantity).toFixed(2)} PLN
                  </span>
                  <span style="color: green;">
                      ${totalPrice} PLN
                  </span>
              `;
            } else priceDisplay.textContent = `${totalPrice} PLN`;
          }
        };
    
        const sizeSelect = document.getElementById('sizeSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('kebabQuantity') as HTMLInputElement;
        
        sizeSelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });
    
    if (choice) {
      const sizeSelect = document.getElementById('sizeSelection') as HTMLSelectElement;
      const meatSelect = document.getElementById('meatSelection') as HTMLSelectElement;
      const sauceSelect = document.getElementById('sauceSelection') as HTMLSelectElement;
      const quantityInput = document.getElementById('kebabQuantity') as HTMLInputElement;
      const selectedSize = sizeSelect.value as Size;
      const baseUnitPrice = meal.prices[selectedSize];
      const quantity = parseInt(quantityInput.value);
      const activePromotion = meal.meal_promotions.find(p => p.sizes.includes(selectedSize));
      const finalUnitPrice = activePromotion 
        ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100) 
        : baseUnitPrice;
      const finalTotalPrice = (finalUnitPrice * quantity).toFixed(2);

      console.log(sizeSelect.value + ' ' + meatSelect.value + ' ' + sauceSelect.value + ' ' + quantityInput.value + ' ' + finalTotalPrice);
    }
  }

  async startChoosingBeverage(): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac napoj' : 'Please select beverage';

    const beverageNamesOriginal = this.beverages.map(beverage => beverage.name).sort();

    const beverageNamesTranslated = this.beverages.map(beverage => {
      let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);
      if (!this.isBeverageTranslationAvailable(beverage.name)) beverageNameTranslated = beverage.name;
      return beverageNameTranslated;
    }).sort();

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' ? `Wybierz interesujacy Cie napoj` : `Choose interesting beverage`,
      input: "radio",
      inputOptions: beverageNamesTranslated,
      background: '#141414',
      color: 'white',
      confirmButtonText: 'Ok',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      inputValidator: (value) => {
        if (!value) {
          return errorText;
        }
        return null;
      },
    });
    
    if (choice) {
      this.startChoosingBeverageProperties(beverageNamesOriginal[choice])
    }
  }

  async startChoosingBeverageProperties(beverageName: string): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverageName);

    if (!this.isBeverageTranslationAvailable(beverageName)) {
      beverageNameTranslated = beverageName;
    }

    const beverages = this.beverages.filter(beverage => beverage.name === beverageName);
    const beverageCapacities = beverages.map(beverage => {
        return `<option value="${beverage.capacity}">${beverage.capacity} L</option>`;
      }).join('');

    const selectCapacityHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz pojemnosc</h3>` : `<h3>Select capacity</h3>`;
    const howManyHeading = this.langService.currentLang === 'pl' ? `<h3>Jaka ilosc?</h3>` : `<h3>How many?</h3>`;

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: `<span style="color: red;">${beverageNameTranslated}</span>`,
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj do zamowienia' : 'Add to order',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      html: `
        <style>
          input[type="number"], select {
            color: white;
            text-align: center;
            background-color: #141414;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
            box-shadow: none !important;
          }

          select:focus, input[type="number"]:focus {
            border: 1px solid red !important; 
          }
        </style>

        <div style="margin-bottom: 20px;">
          ${selectCapacityHeading}
          <select id="capacitySelection" class="swal2-input">
            ${beverageCapacities}
          </select>
        </div>
        
        <div style="margin-bottom: 20px;">
          ${howManyHeading}
          <input id="beverageQuantity" class="swal2-input" type="number" value="1" min="1" max=10 style="width: 50%; text-align: center;">
        </div>

        <div style="margin-bottom: 20px;">
          <h4>
            <span id="beveragePrice">${beverages[0].price}</span>
          </h4>
        </div>
      `,
      didOpen: () => {
        const updatePrice = () => {
          const capacitySelect = document.getElementById('capacitySelection') as HTMLSelectElement;
          const quantityInput = document.getElementById('beverageQuantity') as HTMLInputElement;
          
          const selectedCapacity = capacitySelect.value;
          const beverage = beverages.find(b => b.capacity.toString() === selectedCapacity);
          
          if (!beverage) {
              console.error('Beverage with this capacity not found!');
              return;
          }
      
          const baseUnitPrice = beverage.price;
          const quantity = parseInt(quantityInput.value) || 1;

          let finalUnitPrice = baseUnitPrice;
          let activePromotion = null;
      
          if (beverage.promotion) {
              finalUnitPrice = baseUnitPrice * (1 - beverage.promotion.discount_percentage / 100);
              activePromotion = beverage.promotion;
          }
      
          const totalPrice = (finalUnitPrice * quantity).toFixed(2);
          const priceDisplay = document.getElementById('beveragePrice');
      
          if (priceDisplay) {
              if (activePromotion) {
                  priceDisplay.innerHTML = `
                      <span style="text-decoration: line-through; color: red; margin-right: 10px;">
                          ${(baseUnitPrice * quantity).toFixed(2)} PLN
                      </span>
                      <span style="color: green;">
                          ${totalPrice} PLN
                      </span>
                  `;
              } else priceDisplay.textContent = `${totalPrice} PLN`;
          }
        };
    
        const capacitySelect = document.getElementById('capacitySelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('beverageQuantity') as HTMLInputElement;
        
        capacitySelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      inputValidator: (value) => {

      },
    });
    
    if (choice) {
      const capacitySelect = document.getElementById('capacitySelection') as HTMLSelectElement;
      const quantityInput = document.getElementById('beverageQuantity') as HTMLInputElement;
      const selectedCapacity = capacitySelect.value;
      const beverage = beverages.find(b => b.capacity.toString() === selectedCapacity);

      if (!beverage) {
        console.error('Beverage with this capacity not found!');
        return;
      }

      const baseUnitPrice = beverage.price;
      const quantity = parseInt(quantityInput.value);
      const activePromotion = beverage.promotion;
      const finalUnitPrice = activePromotion 
        ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100) 
        : baseUnitPrice;
      const finalTotalPrice = (finalUnitPrice * quantity).toFixed(2);

      console.log(selectedCapacity + ' ' + quantityInput.value + ' ' + finalTotalPrice);
    }
  } 

  async startChoosingAddonProperties(): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const addonNames = this.addons.map(addon => {
        return `<option value="${addon.name}">${addon.name}</option>`;
      }).join('');

    const selectAddonHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz dodatek</h3>` : `<h3>Select addon</h3>`;
    const howManyHeading = this.langService.currentLang === 'pl' ? `<h3>Jaka ilosc?</h3>` : `<h3>How many?</h3>`;

    const { value: choice } = await Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj do zamowienia' : 'Add to order',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      customClass: {
        validationMessage: 'custom-validation-message',
        input: 'swal-radio-container-orders'
      },
      html: `
        <style>
          input[type="number"], select {
            color: white;
            text-align: center;
            background-color: #141414;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
            box-shadow: none !important;
          }

          select:focus, input[type="number"]:focus {
            border: 1px solid red !important; 
          }
        </style>

        <div style="margin-bottom: 20px;">
          ${selectAddonHeading}
          <select id="addonSelection" class="swal2-input">
            ${addonNames}
          </select>
        </div>
        
        <div style="margin-bottom: 20px;">
          ${howManyHeading}
          <input id="addonQuantity" class="swal2-input" type="number" value="1" min="1" max=10 style="width: 50%; text-align: center;">
        </div>

        <div style="margin-bottom: 20px;">
          <h4>
            <span id="addonPrice">${this.addons[0].price}</span>
          </h4>
        </div>
      `,
      didOpen: () => {
        const updatePrice = () => {
          const addonSelect = document.getElementById('addonSelection') as HTMLSelectElement;
          const quantityInput = document.getElementById('addonQuantity') as HTMLInputElement;
          const addon = this.addons.find(addon => addon.name === addonSelect.value);
          
          if (!addon) {
              console.error('Addon with this name not found!');
              return;
          }
      
          const baseUnitPrice = addon.price;
          const quantity = parseInt(quantityInput.value) || 1;

          let finalUnitPrice = baseUnitPrice;
          let activePromotion = null;
      
          if (addon.promotion) {
              finalUnitPrice = baseUnitPrice * (1 - addon.promotion.discount_percentage / 100);
              activePromotion = addon.promotion;
          }
      
          const totalPrice = (finalUnitPrice * quantity).toFixed(2);
          const priceDisplay = document.getElementById('addonPrice');
      
          if (priceDisplay) {
              if (activePromotion) {
                  priceDisplay.innerHTML = `
                      <span style="text-decoration: line-through; color: red; margin-right: 10px;">
                          ${(baseUnitPrice * quantity).toFixed(2)} PLN
                      </span>
                      <span style="color: green;">
                          ${totalPrice} PLN
                      </span>
                  `;
              } else priceDisplay.textContent = `${totalPrice} PLN`;
          }
        };
    
        const addonSelect = document.getElementById('addonSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('addonQuantity') as HTMLInputElement;
        
        addonSelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      inputValidator: (value) => {

      },
    });
    
    if (choice) {
      const addonSelect = document.getElementById('addonSelection') as HTMLSelectElement;
      const quantityInput = document.getElementById('addonQuantity') as HTMLInputElement;
      const addon = this.addons.find(addon => addon.name === addonSelect.value);

      if (!addon) {
        console.error('Addon with this name not found!');
        return;
      }

      const baseUnitPrice = addon.price;
      const quantity = parseInt(quantityInput.value) || 1;
      const activePromotion = addon.promotion;
      const finalUnitPrice = activePromotion 
        ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100) 
        : baseUnitPrice;
      const finalTotalPrice = (finalUnitPrice * quantity).toFixed(2);

      console.log(addonSelect.value + ' ' + quantityInput.value + ' ' + finalTotalPrice);
    }
  } 

  isMealTranslationAvailable(mealName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.' + mealName);
    return translatedName !== 'menu.meals.' + mealName;
  }

  isIngredientTranslationAvailable(ingredientName: string): boolean {
    const translatedName = this.translate.instant('menu.meals.ingredients.' + ingredientName);
    return translatedName !== 'menu.meals.ingredients.' + ingredientName;
  }

  isBeverageTranslationAvailable(beverageName: string): boolean {
    const translatedName = this.translate.instant('menu.beverages.' + beverageName);
    return translatedName !== 'menu.beverages.' + beverageName;
  }

  isAddonTranslationAvailable(addonName: string): boolean {
    const translatedName = this.translate.instant('menu.addons.' + addonName);
    return translatedName !== 'menu.addons.' + addonName;
  }
}
