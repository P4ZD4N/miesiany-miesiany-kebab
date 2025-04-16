import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LangService } from '../lang/lang.service';
import { MenuService } from '../menu/menu.service';
import { AddonResponse, BeverageResponse, IngredientResponse, MealPromotion, MealResponse } from '../../responses/responses';
import Swal from 'sweetalert2';
import { Size } from '../../enums/size.enum';
import { NewOrderRequest } from '../../requests/requests';
import { OrdersService } from '../orders/orders.service';
import { OrderType } from '../../enums/order-type.enum';
import { OrderStatus } from '../../enums/order-status.enum';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  storageKey = 'orderData';

  beverages: BeverageResponse[] = [];
  addons: AddonResponse[] = [];
  meals: MealResponse[] = [];
  ingredients: IngredientResponse[] = [];

  trackOrderData = {
    orderId: 0,
    customerPhone: ''
  }

  order: NewOrderRequest = {
    order_type: null, 
    order_status: null,
    customer_phone: '',
    customer_email: '',
    meals: {},
    beverages: {},
    addons: {},
    total_price: 0
  };

  constructor(
    private translate: TranslateService,
    private langService: LangService,
    private menuService: MenuService,
    private ordersService: OrdersService,
    private router: Router
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

  selectNextOrderItem(isNewOrder = false): void {

    if (!isNewOrder) {
      const order = this.getOrderData();

      if (order && order.total_price > 0) {
          this.askWhetherReturnToPreviousOrder();
          return;
      }
    }
  
    this.initRequiredData();

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' ? `Co chcesz zamowic?` : `What do you want to order?`,
      background: '#141414',
      color: 'white',
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? '🥤 Napoje' : '🥤 Beverages',
      denyButtonColor: '#33acff',
      showCancelButton: true,
      cancelButtonText: this.langService.currentLang === 'pl' ? '🧂 Dodatki' : '🧂 Addons',
      cancelButtonColor: '#198754',
      confirmButtonText: this.langService.currentLang === 'pl' ? '🍽️ Dania' : '🍽️ Meals',
      confirmButtonColor: 'red',
      customClass: {
        actions: 'swal-actions-vertical',  
        confirmButton: 'swal-confirm-btn',
        denyButton: 'swal-deny-btn',
        cancelButton: 'swal-cancel-btn'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.startChoosingMeal()
      } else if (result.isDenied) {
        this.startChoosingBeverage();
      } else if (result.isDismissed) {
        this.startChoosingAddonProperties();
      }
    });
  }

  askWhetherReturnToPreviousOrder(): void {

    this.initRequiredData();

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' 
        ? `Czy chcesz wrocic do poprzedniego zamowienia?` 
        : `Do you want to return to your previous order?`,
      background: '#141414',
      color: 'white',
      showCancelButton: true,
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Nie, zloz nowe zamowienie' : 'No, place new order',
      cancelButtonColor: 'red',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak, chce kontynuowac' : 'Yes, I want to continue',
      confirmButtonColor: '#198754',
      customClass: {
        actions: 'swal-actions-vertical',  
        confirmButton: 'swal-confirm-btn',
        denyButton: 'swal-deny-btn',
        cancelButton: 'swal-cancel-btn'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const existingOrder = this.getOrderData();
  
        if (existingOrder) {
          this.order = {
            order_type: existingOrder.order_type ?? null, 
            order_status: existingOrder.order_status ?? null,
            customer_phone: existingOrder.customer_phone ?? '',
            customer_email: existingOrder.customer_email ?? '',
            meals: existingOrder.meals ?? {},
            beverages: existingOrder.beverages ?? {},
            addons: existingOrder.addons ?? {},
            total_price: existingOrder.total_price ?? 0
          };
        }
        
        this.askWhetherOrderComplete();
      } else if (result.isDismissed) {
        this.clearOrderData();
        this.order = {
          order_type: null, 
          order_status: null,
          customer_phone: '',
          customer_email: '',
          meals: {},
          beverages: {},
          addons: {},
          total_price: 0
        };

        this.selectNextOrderItem(true);
      }
    });
  }

  initRequiredData(): void {
    if (this.beverages.length === 0) this.loadBeverages();
    if (this.addons.length === 0) this.loadAddons();
    if (this.meals.length === 0) this.loadMeals();
    if (this.ingredients.length === 0)this.loadIngredients();
  }

  async startChoosingMeal(): Promise<void> {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac danie' : 'Please select meal';
    const mealNamesOriginal = this.meals.map(meal => meal.name).sort((a, b) => a.localeCompare(b, this.langService.currentLang));
    const mealNamesTranslated = this.meals.map(meal => {
      let mealNameTranslated = this.translate.instant('menu.meals.' + meal.name);
      if (!this.isMealTranslationAvailable(meal.name)) mealNameTranslated = meal.name;
      return mealNameTranslated;
    }).sort((a, b) => a.localeCompare(b, this.langService.currentLang));

    const { value: choice, isDenied } = await Swal.fire({
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
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
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
    
    if (isDenied) {
      this.selectNextOrderItem(true);
    }

    if (choice) {
      this.startChoosingMealProperties(mealNamesOriginal[choice])
    }
  }

  async startChoosingMealProperties(mealName: string): Promise<void> {

    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
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

    const mealSizes = Object.entries(meal.prices)
      .filter(([size, price]) => price)
      .map(([size, price]) => {
        let sizeTranslated = this.translate.instant('menu.meals.sizes.' + size);
        return `<option value="${size}" data-price="${price}">${sizeTranslated}</option>`;
      }).join('');;

    const meats = this.ingredients
      .filter(ingredient => ingredient.ingredient_type === 'MEAT')
      .map(ingredient => {
        let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

        if (!this.isIngredientTranslationAvailable(ingredient.name)) {
          ingredientNameTranslated = ingredient.name;
        }

        return `<option value="${ingredient.name}">${ingredientNameTranslated}</option>`;
      }).sort((a, b) => a.localeCompare(b, this.langService.currentLang));

    const sauces = this.ingredients
      .filter(ingredient => ingredient.ingredient_type === 'SAUCE')
      .map(ingredient => {
        let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredient.name);

        if (!this.isIngredientTranslationAvailable(ingredient.name)) {
          ingredientNameTranslated = ingredient.name;
        }

      return `<option value="${ingredient.name}">${ingredientNameTranslated}</option>`;
    }).sort((a, b) => a.localeCompare(b, this.langService.currentLang));

    const ingredientsHeading = this.langService.currentLang === 'pl' ? `<h3>Skladniki</h3>` : `<h3>Ingredients</h3>`;
    const selectSizeHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz rozmiar</h3>` : `<h3>Select size</h3>`;
    const selectMeatHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz mieso</h3>` : `<h3>Select meat</h3>`;
    const selectSauceHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz sos</h3>` : `<h3>Select sauce</h3>`;
    const howManyHeading = this.langService.currentLang === 'pl' ? `<h3>Jaka ilosc?</h3>` : `<h3>How many?</h3>`;

    const { value: choice, isDenied } = await Swal.fire({
      allowOutsideClick: false,
      title: `<span style="color: red;">${mealNameTranslated}</span>`,
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj do zamowienia' : 'Add to order',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
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
                      ${(baseUnitPrice * quantity).toFixed(2)} zl
                  </span>
                  <span style="color: green;">
                      ${totalPrice} zl
                  </span>
              `;
            } else priceDisplay.textContent = `${totalPrice} zl`;
          }
        };
    
        const sizeSelect = document.getElementById('sizeSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('kebabQuantity') as HTMLInputElement;
        
        sizeSelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      preConfirm: () => {
        const sizeSelect = document.getElementById('sizeSelection') as HTMLSelectElement;
        const meatSelect = document.getElementById('meatSelection') as HTMLSelectElement;
        const sauceSelect = document.getElementById('sauceSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('kebabQuantity') as HTMLInputElement;
        let quantity = parseInt(quantityInput.value);
        const mealKey = mealName + '_' + meatSelect.value + '_' + sauceSelect.value;
    
        if (isNaN(quantity) || quantity > 20) {
            const quantityErrorText = this.langService.currentLang === 'pl' 
              ? `Nie mozna dodac wiecej niz 20 porcji tego dania!`
              : `You can't add more than 20 portions of this meal!`;
    
            Swal.showValidationMessage(quantityErrorText);
            return false;
        }

        if (isNaN(quantity) || quantity < 1) {
          const quantityErrorText = this.langService.currentLang === 'pl' 
            ? `Nie mozna dodac mniej niz 1 porcji tego dania!`
            : `You can't add less than 1 portion of this meal!`;
  
          Swal.showValidationMessage(quantityErrorText);
          return false;
        }

        if (this.order.meals[mealKey] && this.order.meals[mealKey][sizeSelect.value as Size] + quantity > 20) {
            const errorText = this.langService.currentLang === 'pl'
              ? `Nie mozna dodac wiecej niz 20 porcji tego dania (obecnie juz wybrano ${this.order.meals[mealKey][sizeSelect.value as Size]})!`
              : `You can't add more than 20 portions of this meal (already selected: ${this.order.meals[mealKey][sizeSelect.value as Size]})!`;
      
            Swal.showValidationMessage(errorText);
            return false;
        }

        return { quantity }; 
      }
    });

    if (isDenied) {
      this.startChoosingMeal();
    }
    
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
      const mealKey = mealName + '_' + meatSelect.value + '_' + sauceSelect.value;

      if (!this.order.meals[mealKey]) {
        this.order.meals[mealKey] = {
          [Size.SMALL]: 0,
          [Size.MEDIUM]: 0,
          [Size.LARGE]: 0,
          [Size.XL]: 0
        };
      }

      this.order.meals[mealKey][selectedSize] += quantity;
      this.order.total_price += parseFloat(finalTotalPrice);

      this.setOrderData(this.order);
      this.askWhetherOrderComplete(mealNameTranslated);
    }
  }

  async startChoosingBeverage(): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const errorText = this.langService.currentLang === 'pl' ? 'Prosze wybrac napoj' : 'Please select beverage';

    const beverageNamesOriginal = this.beverages
      .map(beverage => beverage.name)
      .sort((a, b) => a.localeCompare(b, this.langService.currentLang))
      .filter((value, index, self) => self.indexOf(value) === index);

    const beverageNamesTranslated = this.beverages
      .map(beverage => {
        let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverage.name);
        if (!this.isBeverageTranslationAvailable(beverage.name)) beverageNameTranslated = beverage.name;
        return beverageNameTranslated;
      })
      .sort((a, b) => a.localeCompare(b, this.langService.currentLang))
      .filter((value, index, self) => self.indexOf(value) === index);

    const { value: choice, isDenied } = await Swal.fire({
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
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
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
    
    if (isDenied) {
      this.selectNextOrderItem(true);
    }

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

    const { value: choice, isDenied } = await Swal.fire({
      allowOutsideClick: false,
      title: `<span style="color: red;">${beverageNameTranslated}</span>`,
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj do zamowienia' : 'Add to order',
      confirmButtonColor: '#198754',
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
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
                          ${(baseUnitPrice * quantity).toFixed(2)} zl
                      </span>
                      <span style="color: green;">
                          ${totalPrice} zl
                      </span>
                  `;
              } else priceDisplay.textContent = `${totalPrice} zl`;
          }
        };
    
        const capacitySelect = document.getElementById('capacitySelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('beverageQuantity') as HTMLInputElement;
        
        capacitySelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      preConfirm: () => {
        const capacitySelect = document.getElementById('capacitySelection') as HTMLSelectElement;
        const selectedCapacity = capacitySelect.value;
        const quantityInput = document.getElementById('beverageQuantity') as HTMLInputElement;
        let quantity = parseInt(quantityInput.value);
        const beverage = beverages.find(b => b.capacity.toString() === selectedCapacity);
    
        if (!beverage) {
          console.error('Beverage with this capacity not found!');
          return;
        }

        if (isNaN(quantity) || quantity > 10) {
            const quantityErrorText = this.langService.currentLang === 'pl' 
              ? `Nie mozna dodac wiecej niz 10 sztuk tego napoju!`
              : `You can't add more than 10 units of this beverage!`;


            Swal.showValidationMessage(quantityErrorText);
            return false;
        }
        
        if (isNaN(quantity) || quantity < 1 ) {
          const quantityErrorText = this.langService.currentLang === 'pl' 
            ? `Nie mozna dodac mniej niz 1 sztuk tego napoju!`
            : `You can't add less than 1 units of this beverage!`;


          Swal.showValidationMessage(quantityErrorText);
          return false;
        }

        if (
          this.order.beverages[beverage.name] && 
          this.order.beverages[beverage.name][beverage.capacity] + quantity > 10
        ) {
          const errorText = this.langService.currentLang === 'pl'
          ? `Nie mozna dodac wiecej niz 10 sztuk tego napoju (obecnie juz wybrano ${this.order.beverages[beverage.name][beverage.capacity]})!`
          : `You can't add more than 10 units of this beverage (already selected: ${this.order.beverages[beverage.name][beverage.capacity]})!`;
    
          Swal.showValidationMessage(errorText);
          return false;
        }
    
        return { quantity }; 
      }
    });
    
    if (isDenied) {
      this.startChoosingBeverage();
    }

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

      if (!this.order.beverages[beverage.name]) {
        this.order.beverages[beverage.name] = {}; 
      }
      
      if (!this.order.beverages[beverage.name][beverage.capacity]) {
        this.order.beverages[beverage.name][beverage.capacity] = 0;
      }

      this.order.beverages[beverage.name][beverage.capacity] += quantity;
      this.order.total_price += parseFloat(finalTotalPrice);

      this.setOrderData(this.order);
      this.askWhetherOrderComplete(beverageNameTranslated);
    }
  } 

  async startChoosingAddonProperties(): Promise<void> {
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const addonNames = this.addons.map(addon => {
        let addonNameTranslated = this.translate.instant('menu.addons.' + addon.name);
        if (!this.isAddonTranslationAvailable(addon.name)) {
          addonNameTranslated = addon.name;
        }
        return `<option value="${addon.name}">${addonNameTranslated}</option>`;
      }).sort((a, b) => a.localeCompare(b, this.langService.currentLang));

    const selectAddonHeading = this.langService.currentLang === 'pl' ? `<h3>Wybierz dodatek</h3>` : `<h3>Select addon</h3>`;
    const howManyHeading = this.langService.currentLang === 'pl' ? `<h3>Jaka ilosc?</h3>` : `<h3>How many?</h3>`;

    const { value: choice, isDenied } = await Swal.fire({
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
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
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
                          ${(baseUnitPrice * quantity).toFixed(2)} zl
                      </span>
                      <span style="color: green;">
                          ${totalPrice} zl
                      </span>
                  `;
              } else priceDisplay.textContent = `${totalPrice} zl`;
          }
        };
    
        const addonSelect = document.getElementById('addonSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('addonQuantity') as HTMLInputElement;
        
        addonSelect.addEventListener('change', updatePrice);
        quantityInput.addEventListener('change', updatePrice);
        quantityInput.addEventListener('input', updatePrice);

        updatePrice();
      },
      preConfirm: () => {
        const addonSelect = document.getElementById('addonSelection') as HTMLSelectElement;
        const quantityInput = document.getElementById('addonQuantity') as HTMLInputElement;
        let quantity = parseInt(quantityInput.value);
        const addon = this.addons.find(addon => addon.name === addonSelect.value);

        if (!addon) {
          console.error('Addon with this name not found!');
          return;
        }

        if (isNaN(quantity) || quantity > 10) {
          const errorText = this.langService.currentLang === 'pl'
            ? `Nie mozna dodac wiecej niz 10 sztuk tego dodatku!`
            : `You can't add more than 10 units of this addon!`;
  
          Swal.showValidationMessage(errorText);
          return false;
        }

        if (this.order.addons[addon.name] && this.order.addons[addon.name] + quantity > 10) {
          const errorText = this.langService.currentLang === 'pl'
            ? `Nie mozna dodac wiecej niz 10 sztuk tego dodatku (obecnie juz wybrano ${this.order.addons[addon.name]})!`
            : `You can't add more than 10 units of this beverage (already selected: ${this.order.addons[addon.name]})!`;
        
            Swal.showValidationMessage(errorText);
            return false;
        }

        if (isNaN(quantity) || quantity < 1 ) {
          const errorText = this.langService.currentLang === 'pl'
            ? `Nie mozna dodac mniej niz 1 sztuki tego dodatku!`
            : `You can't add less than 1 unit of this addon!`;
  
          Swal.showValidationMessage(errorText);
          return false;
        }
    
        return { quantity }; 
      }
    });

    if (isDenied) {
      this.selectNextOrderItem(true);
    }
    
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

      if (!this.order.addons[addon.name]) {
        this.order.addons[addon.name] = 0; 
      }

      this.order.addons[addon.name] += quantity;
      this.order.total_price += parseFloat(finalTotalPrice);

      this.setOrderData(this.order);

      const addonNameTranslated = this.isAddonTranslationAvailable(addon.name)
        ? this.translate.instant('menu.addons.' + addon.name)
        : addon.name;
      
      this.askWhetherOrderComplete(addonNameTranslated);
    }
  } 

  askWhetherOrderComplete(itemName?: string): void {

    let orderMealsList = Object.keys(this.order.meals).map(mealNameWithDetails => {
      const parts = mealNameWithDetails.split('_');

      const mealName = this.isMealTranslationAvailable(parts[0])
        ? this.translate.instant('menu.meals.' + parts[0])
        : parts[0];

      const meatName = this.isIngredientTranslationAvailable(parts[1])
        ? this.translate.instant('menu.meals.ingredients.' + parts[1])
        : parts[1];

      const sauceName = this.isIngredientTranslationAvailable(parts[2])
        ? this.translate.instant('menu.meals.ingredients.' + parts[2])
        : parts[2];

      const mealSizes = Object.entries(this.order.meals[mealNameWithDetails])
        .filter(([size, quantity]) => quantity > 0) 
        .map(([size, quantity]) => {
          const meal = this.meals.filter(meal => meal.name === parts[0])[0];
          const baseUnitPrice = meal.prices[size as Size];
          const activePromotion = meal.meal_promotions.find(p => p.sizes.includes(size as Size));
          const finalUnitPrice = activePromotion 
            ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100) 
            : baseUnitPrice;
          const translatedSize = this.translate.instant('menu.meals.sizes.' + size);
          return `
            <div class="meal-size-item">${translatedSize}: 
              <span class="quantity">
                <span class=meal-quantity>${quantity}x ${finalUnitPrice.toFixed(2)} zl</span>
                <button class="meal-plus" meal-name="${mealNameWithDetails}" meal-size="${size}">+</button>
                <button class="meal-minus" meal-name="${mealNameWithDetails}" meal-size="${size}">−</button>
              </span>
            </div>`;
        })
        .join('');

        return `
          <div class="meal-item">
            <div class="meal-name">${mealName}</div>
            <div class="meal-details">
              <span class="meal-ingredient">${meatName}</span>, 
              <span class="meal-ingredient">${sauceName}</span>
            </div>
            <div class="meal-sizes">${mealSizes}</div>
          </div>`;
    }).join('');

    let orderBeveragesList = Object.keys(this.order.beverages).map(beverageNameWithDetails => {
      const beverageName = this.isBeverageTranslationAvailable(beverageNameWithDetails)
        ? this.translate.instant('menu.beverages.' + beverageNameWithDetails)
        : beverageNameWithDetails;

      const beverageCapacities = Object.entries(this.order.beverages[beverageNameWithDetails])
        .filter(([capacity, quantity]) => quantity > 0) 
        .map(([capacity, quantity]) => {
          const beverage = this.beverages.find(b => b.name === beverageNameWithDetails && b.capacity === Number(capacity));
          if (!beverage) return '';
          const baseUnitPrice = beverage.price;
          const finalUnitPrice = beverage.promotion 
            ? baseUnitPrice * (1 - beverage.promotion.discount_percentage / 100) 
            : baseUnitPrice;
          return `
            <div class="beverage-capacity-item">
              ${capacity} L: 
              <span class="quantity">
                <span class="beverage-quantity">${quantity}x ${finalUnitPrice.toFixed(2)} zl</span>
                <button class="beverage-plus" beverage-name="${beverageNameWithDetails}" beverage-capacity="${capacity}">+</button>
                <button class="beverage-minus" beverage-name="${beverageNameWithDetails}" beverage-capacity="${capacity}">−</button>
              </span>
            </div>`;
        })
        .join('');

      return `
        <div class="beverage-item">
          <div class="beverage-name">${beverageName}</div>
          <div class="beverage-capacities">${beverageCapacities}</div>
        </div>`;
    }).join('');

    let orderAddonsList = Object.entries(this.order.addons)
      .filter(([addonName, quantity]) => quantity > 0)
      .map(([addonName, quantity]) => {
        const addon = this.addons.filter(addon => addon.name === addonName)[0];
        const baseUnitPrice = addon.price;
        const finalUnitPrice = addon.promotion 
        ? baseUnitPrice * (1 - addon.promotion.discount_percentage / 100) 
        : baseUnitPrice;
        const addonNameTranslated = this.isAddonTranslationAvailable(addonName)
          ? this.translate.instant('menu.addons.' + addonName)
          : addonName;

        return `
          <div class="addon-item">
            ${addonNameTranslated}
            <span class="quantity">
              <span class="addon-quantity">${quantity}x ${finalUnitPrice.toFixed(2)} zl</span>
              <button class="addon-plus" data-addon="${addonName}">+</button>
              <button class="addon-minus" data-addon="${addonName}">−</button>
            </span>
          </div>`;
      }).join('');

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Czy to juz wszystko?</span>` : 
        `<span style="color: red;">Is that everything?</span>`,
        text: this.langService.currentLang === 'pl'
        ? itemName 
          ? `Pomyslnie dodano '${itemName}' do twojego zamowienia! Co dalej?`
          : `Produkt został dodany do twojego zamówienia! Co dalej?`
        : itemName
          ? `Successfully added '${itemName}' to your order! What's next?`
          : `Item has been added to your order! What's next?`,
      color: 'white',
      background: '#141414',
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Przejdz dalej' : 'Next step',
      denyButtonColor: '#33acff',
      showCancelButton: true,
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Wroc pozniej' : 'Come back later',
      cancelButtonColor: 'red',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj kolejne' : 'Add more',
      confirmButtonColor: '#198754',
      customClass: {
        actions: 'swal-actions-vertical',  
        confirmButton: 'swal-confirm-btn',
        denyButton: 'swal-deny-btn',
        cancelButton: 'swal-cancel-btn'
      },
      html: `
        <style>
          .order-summary {
            margin: 15px 0 0 0;
            text-align: left;
            max-height: 300px;
            overflow-y: auto;
            padding-right: 5px;
            scrollbar-color: #ff0000 #f1f1f1; 
          }

          .addon-plus, .addon-minus, .beverage-plus, .beverage-minus, .meal-plus, .meal-minus {
            color: red;
            background: inherit;
            border: none;
          }

          .addon-plus:hover, 
          .addon-minus:hover, 
          .beverage-plus:hover, 
          .beverage-minus:hover,
          .meal-plus:hover, 
          .meal-minus:hover {
            color: white;
          }

          .total-price {
            margin: 15px 0 0 0;
          }
          
          .meals-title, .beverages-title, .addons-title {
            color: red;
            margin-bottom: 10px;
            text-align: center;
            font-size: 1.1em;
          }
          
          .price {
            text-align: center;
            font-size: 1.2em;
          } 

          .meal-item, .beverage-item, .addon-item {
            background: rgba(0,0,0,0.3);
            border-radius: 8px;
            padding: 12px;
            margin-bottom: 10px;
            border-left: 3px solid red;
          }
          
          .meal-name, .beverage-name, .addon-name {
            font-size: 1.05em;
            color: #fff;
          }
          
          .meal-details {
            margin: 5px 0;
            color: #ccc;
          }
          
          .meal-ingredient {
            color: #ddd;
          }
          
          .meal-sizes, .beverage-capacities {
            margin-top: 5px;
            color: red;
          }

          .meal-size-item, .beverage-capacity-item {
            padding: 3px 0;
            border-bottom: 1px solid rgba(255,255,255,0.1);
          }

          .meal-size-item:last-child, .beverage-capacity-item:last-child {
            border-bottom: none;
          }

          .quantity, .capacity {
            float: right;
            color: #fff;
          }
        </style>

        <div class="order-summary">
          ${orderMealsList 
            ? `<div class="meals-title">${this.langService.currentLang === 'pl' ? 'Dania' : 'Meals'}</div>${orderMealsList}` 
            : ''}
          ${orderBeveragesList 
            ? `<div class="beverages-title">${this.langService.currentLang === 'pl' ? 'Napoje' : 'Beverages'}</div>${orderBeveragesList}` 
            : ''}
          ${orderAddonsList 
            ? `<div class="addons-title">${this.langService.currentLang === 'pl' ? 'Dodatki' : 'Addons'}</div>${orderAddonsList}` 
            : ''}
        </div>
        <div class="total-price" id="total-price">
          ${this.order.total_price > 0
            ? `<div class="price"> ${this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: '} ${this.order.total_price.toFixed(2)} zl</div>` 
            : ''}
        </div>
      `,
    }).then((result) => {
      if (result.isConfirmed) {
        this.selectNextOrderItem(true);
      } else if (result.isDenied) {
        this.chooseFormOfOrderReceiving()
      }
    });

    setTimeout(() => {
      document.querySelectorAll('.addon-plus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const addonName = target.getAttribute('data-addon');

          if (addonName) {
            if (this.order.addons[addonName] < 10) {
              this.order.addons[addonName]++;

              const addonQuantityElement = target.closest('.addon-item')?.querySelector('.addon-quantity') as HTMLElement;
              const priceContainer = document.getElementById('total-price');
              if (addonQuantityElement && priceContainer) {
                const quantity = this.order.addons[addonName];
                const addon = this.addons.find(addon => addon.name === addonName);  
                const finalUnitPrice = addon?.promotion 
                  ? addon.price * (1 - addon.promotion.discount_percentage / 100)
                  : addon?.price;
                addonQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;

                this.order.total_price += Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
            }
          }
        });
      });

      document.querySelectorAll('.beverage-plus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const beverageName = target.getAttribute('beverage-name');
          const beverageCapacity = target.getAttribute('beverage-capacity');

          if (beverageName && beverageCapacity) {
            if (this.order.beverages[beverageName][Number(beverageCapacity)] < 10) {
              this.order.beverages[beverageName][Number(beverageCapacity)]++;

              const beverageQuantityElement = target.closest('.beverage-capacity-item')?.querySelector('.beverage-quantity') as HTMLElement;
              const priceContainer = document.getElementById('total-price');
              if (beverageQuantityElement && priceContainer) {
                const quantity = this.order.beverages[beverageName][Number(beverageCapacity)];
                const beverage = this.beverages.find(beverage => beverage.name === beverageName);  
                const finalUnitPrice = beverage?.promotion 
                  ? beverage.price * (1 - beverage.promotion.discount_percentage / 100)
                  : beverage?.price;
                beverageQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;

                this.order.total_price += Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
            }
          }
        });
      });

      document.querySelectorAll('.meal-plus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const mealName = target.getAttribute('meal-name');
          const mealSize = target.getAttribute('meal-size');

          if (mealName && mealSize) {
            if (this.order.meals[mealName][mealSize as Size] < 20) {
              this.order.meals[mealName][mealSize as Size]++;

              const mealQuantityElement = target.closest('.meal-size-item')?.querySelector('.meal-quantity') as HTMLElement;
              const priceContainer = document.getElementById('total-price');
              if (mealQuantityElement && priceContainer) {
                const quantity = this.order.meals[mealName][mealSize as Size];
                const meal = this.meals.filter(meal => meal.name === mealName.split("_")[0])[0];
                const baseUnitPrice = meal.prices[mealSize as Size];
                const activePromotion = meal.meal_promotions.find(p => p.sizes.includes(mealSize as Size));
                const finalUnitPrice = activePromotion 
                  ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100)
                  : baseUnitPrice;
                mealQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;


                this.order.total_price += Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
            }
          }
        });
      });
    
      document.querySelectorAll('.addon-minus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const addonName = target.getAttribute('data-addon');
          const priceContainer = document.getElementById('total-price');

          if (addonName) {

            const addon = this.addons.find(addon => addon.name === addonName); 
            const finalUnitPrice = addon?.promotion 
              ? addon.price * (1 - addon.promotion.discount_percentage / 100)
              : addon?.price;

            if (this.order.addons[addonName] > 1) {
              this.order.addons[addonName]--;
              
              const addonQuantityElement = target.closest('.addon-item')?.querySelector('.addon-quantity') as HTMLElement;
              if (addonQuantityElement && priceContainer) {
                const quantity = this.order.addons[addonName];
                
                addonQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;

                this.order.total_price -= Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            } else {
              this.order.total_price -= Number(finalUnitPrice);
              delete this.order.addons[addonName];
  
              if (priceContainer) {
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }
  
              const addonElement = target.closest('.addon-item');
              if (addonElement) {
                addonElement.remove();
              }

              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            }
          } 
        });
      });

      document.querySelectorAll('.beverage-minus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const beverageName = target.getAttribute('beverage-name');
          const beverageCapacity = target.getAttribute('beverage-capacity');

          if (beverageName && beverageCapacity) {

            const beverage = this.beverages.find(beverage => beverage.name === beverageName);  
            const finalUnitPrice = beverage?.promotion 
              ? beverage.price * (1 - beverage.promotion.discount_percentage / 100)
              : beverage?.price;
            const priceContainer = document.getElementById('total-price');

            if (this.order.beverages[beverageName][Number(beverageCapacity)] > 1) {
              this.order.beverages[beverageName][Number(beverageCapacity)]--;

              const beverageQuantityElement = target.closest('.beverage-capacity-item')?.querySelector('.beverage-quantity') as HTMLElement;

              if (beverageQuantityElement && priceContainer) {
                const quantity = this.order.beverages[beverageName][Number(beverageCapacity)];
                beverageQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;

                this.order.total_price -= Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            } else {
              this.order.total_price -= Number(finalUnitPrice);
              delete this.order.beverages[beverageName];

              if (priceContainer) {
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }
  
              const beverageElement = target.closest('.beverage-item');
              if (beverageElement) {
                beverageElement.remove();
              }

              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            }
          }
        });
      });

      document.querySelectorAll('.meal-minus').forEach(button => {
        button.addEventListener('click', (event) => {
          
          const target = event.target as HTMLElement;
          const mealName = target.getAttribute('meal-name');
          const mealSize = target.getAttribute('meal-size');

          if (mealName && mealSize) {

            const quantity = this.order.meals[mealName][mealSize as Size];
            const mealQuantityElement = target.closest('.meal-size-item')?.querySelector('.meal-quantity') as HTMLElement;
            const priceContainer = document.getElementById('total-price');
            const meal = this.meals.filter(meal => meal.name === mealName.split("_")[0])[0];
            const baseUnitPrice = meal.prices[mealSize as Size];
            const activePromotion = meal.meal_promotions.find(p => p.sizes.includes(mealSize as Size));
            const finalUnitPrice = activePromotion 
              ? baseUnitPrice * (1 - activePromotion.discount_percentage / 100)
              : baseUnitPrice;
            mealQuantityElement.innerHTML = `${quantity}x ${finalUnitPrice?.toFixed(2)} zl`;

            if (this.order.meals[mealName][mealSize as Size] > 1) {
              this.order.meals[mealName][mealSize as Size]--;

              if (mealQuantityElement && priceContainer) {
                this.order.total_price -= Number(finalUnitPrice);
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }

              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            } else {
              console.log(this.order.meals);
              this.order.total_price -= Number(finalUnitPrice);
              delete this.order.meals[mealName];

              if (priceContainer) {
                const total = this.order.total_price.toFixed(2);
                const label = this.langService.currentLang === 'pl' ? 'Lacznie: ' : 'Total: ';
                priceContainer.innerHTML = `<div class="price">${label} ${total} zl</div>`;
              }
  
              const mealElement = target.closest('.meal-item');
              if (mealElement) {
                mealElement.remove();
              }

              console.log(this.order.meals);
              this.setOrderData(this.order);
              this.checkIfOrderIsEmpty();
            }
          }
        });
      });
    }, 0);
  }

  checkIfOrderIsEmpty(): void {
    const isMealsEmpty = Object.keys(this.order.meals).length === 0;
    const isAddonsEmpty = Object.keys(this.order.addons).length === 0;
    const isBeveragesEmpty = Object.keys(this.order.beverages).length === 0;
  
    if (isMealsEmpty && isAddonsEmpty && isBeveragesEmpty) Swal.close();
  }

  chooseFormOfOrderReceiving(): void {

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      text: this.langService.currentLang === 'pl' 
        ? `Jak chcesz odebrac zamowienie? Informujemy, ze zamowienia internetowe sa zawsze traktowane jak na wynos!` 
        : `How do you want to receive order? Please note that online orders are always treated as takeaway!`,
      background: '#141414',
      color: 'white',
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? '🍽 W restauracji' : '🍽 In restaurant',
      denyButtonColor: '#33acff',
      showCancelButton: true,
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Wroc pozniej' : 'Come back later',
      cancelButtonColor: 'red',
      confirmButtonText: this.langService.currentLang === 'pl' ? '🚚 W moim domu' : '🚚 In my house',
      confirmButtonColor: '#198754',
      customClass: {
        actions: 'swal-actions-vertical',  
        confirmButton: 'swal-confirm-btn',
        denyButton: 'swal-deny-btn',
        cancelButton: 'swal-cancel-btn'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.startEnteringAddress()
      } else if (result.isDenied) {
        this.startEnteringContactData();
      }
    });
  }

   startEnteringAddress(): void {
    const confirmButtonText = this.langService.currentLang === 'pl' ? 'Dalej' : 'Next step';
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const streetPlaceholder = this.langService.currentLang === 'pl' ? 'Ulica' : 'Street';
    const houseNumberPlaceholder = this.langService.currentLang === 'pl' ? 'Numer domu' : 'House number';
    const postalCodePlaceholder = this.langService.currentLang === 'pl' ? 'Kod pocztowy' : 'Postal code';
    const cityPlaceholder = this.langService.currentLang === 'pl' ? 'Miasto' : 'City';

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Szanowny Kliencie</span>` : 
        `<span style="color: red;">Dear Customer</span>`,
      background: '#141414',
      color: 'white',
      html: `
        <style>
          input[type="text"], input[type="number"] {
            color: white;
            text-align: center;
            background-color: inherit;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
          }

          input[type="text"]:focus, input[type="number"]:focus {
              border: 1px solid red; 
          }

          button {
            margin-top: 10px; 
            background-color: red; 
            color: white; 
            border: none; 
            padding: 10px 20px; 
            border-radius: 5px; 
            cursor: pointer;
          }
        </style>

        <span style="color: white; margin-bottom: 10px;">
          ${this.langService.currentLang === 'pl' 
            ? 'Podaj adres, pod ktory mamy dostarczyc Twoje zamowienie!' 
            : 'Enter address, where we can deliver your order!'}
          <span style="color: red;">
          ${this.langService.currentLang === 'pl' 
            ? 'Dostarczamy zamowienia do 20 km od lokalu. Zamowienie z adresem znajdujacym sie dalej zostanie anulowane. Kazda dostawa kosztuje 15 zl!' 
            : 'We deliver orders up to 20 km from our restaurant. Order with address further away will be canceled. Each delivery costs 15 zl!'}
          </span>
        </span>
        
        <div>
          <input type="text" id="street" maxlength=25  placeholder="${streetPlaceholder}">
        </div>

        <div>
          <input type="number" id="houseNumber" placeholder="${houseNumberPlaceholder}">
        </div>

        <div>
          <input type="text" id="postalCode" maxlength=6  placeholder="${postalCodePlaceholder}">
        </div>

        <div>
          <input type="text" id="city" maxlength=25 placeholder="${cityPlaceholder}">
        </div>
      `,
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showCancelButton: true,
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
      focusConfirm: false,
      customClass: {
        validationMessage: 'custom-validation-message'
      },
      preConfirm: () => {
        const street = (document.getElementById('street') as HTMLInputElement).value;
        const houseNumber = (document.getElementById('houseNumber') as HTMLInputElement).value;
        const postalCode = (document.getElementById('postalCode') as HTMLInputElement).value;
        const city = (document.getElementById('city') as HTMLInputElement).value;

        if (!street || !houseNumber || !postalCode || !city) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' 
              ? 'Wszystkie pola sa wymagane' 
              : 'All fields are required'
          );
          return null;
        }

        if (street.length > 25) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawna ulica!' : 'Invalid street!'
          );
          return null;
        }

        if (Number(houseNumber) < 1) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny numer domu!' : 'Invalid house number!'
          );
          return null;
        }

        const postalCodeRegex = /^[0-9]{2}-[0-9]{3}$/;
        if (!postalCodeRegex.test(postalCode)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny kod pocztowy!' : 'Invalid postal code!'
          );
          return null;
        }

        if (city.length > 25) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawne miasto!' : 'Invalid city!'
          );
          return null;
        }
    
        return { street, houseNumber, postalCode, city };
      }
    }).then((result) => {

      if (result.isDenied) {
        this.chooseFormOfOrderReceiving();
      }

      if (result.isConfirmed && result.value) {
        this.order.street = result.value.street;
        this.order.house_number = result.value.houseNumber;
        this.order.postal_code = result.value.postalCode;
        this.order.city = result.value.city;

        this.setOrderData(this.order);
        this.startEnteringContactData();
      }
    });
  }

  startEnteringContactData(): void {
    const confirmButtonText = this.langService.currentLang === 'pl' ? 'Dalej' : 'Next step';
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const emailPlaceholder = 'Email'
    const phonePlaceholder = this.langService.currentLang === 'pl' ? 'Telefon (9 cyfr)' : 'Phone (9 digits)';

    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Podaj swoje dane kontaktowe!</span>` : 
        `<span style="color: red;">Enter your contact data!</span>`,
      background: '#141414',
      color: 'white',
      html: `
        <style>
          input[type="text"], input[type="email"] {
            color: white;
            text-align: center;
            background-color: inherit;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
          }

          input[type="text"]:focus, input[type="email"]:focus {
              border: 1px solid red; 
          }

          button {
            margin-top: 10px; 
            background-color: red; 
            color: white; 
            border: none; 
            padding: 10px 20px; 
            border-radius: 5px; 
            cursor: pointer;
          }
        </style>

        <span style="color: white; margin-bottom: 10px;">
          ${this.langService.currentLang === 'pl' 
            ? 'Podanie adresu email nie jest wymagane, ale zachecamy do jego wpisania. Kazde zamowienie to rownowartosc 1 punktu! Po zebraniu 10 punktow otrzymasz kod rabatowy na kolejne zamowienie!' 
            : 'Providing an email address is optional, but we encourage you to enter it. Each order is equality of 1 point. After collecting 10 points you’ll receive a discount code for your next order!'}
        </span>
        
        <div>
          <input type="email" id="email" maxlength=30  placeholder="${emailPlaceholder}">
        </div>

        <div>
          <input type="text" id="phone" maxlength=9 placeholder="${phonePlaceholder}">
        </div>
      `,
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showCancelButton: true,
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
      focusConfirm: false,
      customClass: {
        validationMessage: 'custom-validation-message'
      },
      preConfirm: () => {
        const email = (document.getElementById('email') as HTMLInputElement).value;
        const phone = (document.getElementById('phone') as HTMLInputElement).value;

        if (!phone ) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' 
              ? 'Telefon jest wymagany' 
              : 'Phone is required'
          );
          return null;
        }

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
          if (email && !emailRegex.test(email)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny email!' : 'Invalid email!'
          );
          return null;
        }

        const phoneRegex = /^[0-9]{9}$/;
          if (!phoneRegex.test(phone)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny numer telefonu!' : 'Invalid phone number!'
          );
          return null;
        }
    
        return { email, phone};
      }
    }).then((result) => {

      if (result.isDenied) {
        this.chooseFormOfOrderReceiving();
      }

      if (result.isConfirmed && result.value) {
        this.order.customer_email = result.value.email;
        this.order.customer_phone = result.value.phone;

        this.setOrderData(this.order);
        this.startEnteringAdditionalComments();
      }
    });
  }

  startEnteringAdditionalComments(): void {
    const confirmButtonText = this.langService.currentLang === 'pl' ? 'Zamow' : 'Order';
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';

    const additionalCommentsPlaceholder = this.langService.currentLang === 'pl' ? 'Dodatkowe komentarze...' : 'Additional comments...';
    Swal.fire({
      allowOutsideClick: false,
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Przed zlozeniem zamowienia...</span>` : 
        `<span style="color: red;">Before placing order...</span>`,
      background: '#141414',
      color: 'white',
      html: `
        <style>
          textarea {
            color: white;
            text-align: center;
            background-color: inherit;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
          }

          textarea:focus,  {
              border: 1px solid red; 
          }

          button {
            margin-top: 10px; 
            background-color: red; 
            color: white; 
            border: none; 
            padding: 10px 20px; 
            border-radius: 5px; 
            cursor: pointer;
          }
        </style>

        <p style="color: white; margin-bottom: 10px;">
          ${this.langService.currentLang === 'pl' 
            ? 'Czy masz jakies uwagi lub specjalne prosby? Umiesc je w polu ponizej.' 
            : 'Do you have any comments or special requests? Place them in the box below.'}
        </p>

        <div>
          <textarea id="additionalComments" maxlength=100 placeholder="${additionalCommentsPlaceholder}"></textarea>
        </div>
      `,
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showCancelButton: true,
      showDenyButton: true,
      denyButtonText: this.langService.currentLang === 'pl' ? 'Wroc' : 'Go Back',
      denyButtonColor: '#33acff',
      focusConfirm: false,
      customClass: {
        validationMessage: 'custom-validation-message'
      }
    }).then((result) => {

      if (result.isDenied) {
        this.startEnteringContactData();
      }

      if (result.isConfirmed && result.value) {
        const additionalComments = (document.getElementById('additionalComments') as HTMLInputElement).value;
        this.order.additional_comments = additionalComments;
        this.setOrderData(this.order);
        this.placeOrder();
      }
    });
  }

  placeOrder(): void {

    this.order.order_type = OrderType.TAKEAWAY;
    this.order.order_status = OrderStatus.IN_PREPARATION;

    this.ordersService.addOrder(this.order).subscribe({
      next: (response) => {
        Swal.fire({
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
          html: `
            <style>
              ul {
                list-style: none;
              }
            </style>

            <span style="color: white; margin-bottom: 10px;">
              ${this.langService.currentLang === 'pl' 
                ? `Pomyslnie zlozono zamowienie! Mozesz teraz sledzic swoje zamowienie przechodzac pod /track-order i wpisujac dane:`
                : 'Successfully placed order! Now, you can track you order by going to /track-order and entering following details:'}
            </span>
            <ul>
              <li>ID: ${response.id}</li>
              <li>
                ${
                  this.langService.currentLang === 'pl' 
                  ? `Telefon: ${this.order.customer_phone}`
                  : `Phone: ${this.order.customer_phone}`
                }
              </li>
            </ul>
          `,
        });

        this.trackOrderData.orderId = response.id;
        this.trackOrderData.customerPhone = this.order.customer_phone;
        
        this.clearOrderData();
        this.order = {
          order_type: null, 
          order_status: null,
          customer_phone: '',
          customer_email: '',
          meals: {},
          beverages: {},
          addons: {},
          total_price: 0
        };

        this.router.navigate(['/track-order']);
      }
    });
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

  setOrderData(order: NewOrderRequest): void {
    localStorage.setItem(this.storageKey, JSON.stringify(order));
  }

  getOrderData(): NewOrderRequest | null {
    const data = localStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : null;
  }

  clearOrderData(): void {
    localStorage.removeItem(this.storageKey);
  }
}
