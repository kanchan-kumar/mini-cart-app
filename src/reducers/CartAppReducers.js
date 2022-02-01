import {combineReducers} from "redux";
import _ from "lodash";
import {saveCartDataInLocalStorage} from "../apis/productApi";

const productReducers = (state = [], action) => {
     switch (action.type) {
          case 'FETCH_PRODUCTS':
               return [...action.payload];
          case 'UPDATE_PRODUCT':
               return state.map(product => {
                    if (product.id === action.payload.id) {
                         return {...action.payload};
                    }
                    return product;
               });
          case 'INCREASE_PRODUCT_QUANTITY':
               return [...state];
          case 'DECREASE_PRODUCT_QUANTITY':
               return [...state];

          default:
               return state;
     }
};

const cartReducers = (state = [], action) => {
     switch (action.type) {
          case 'FETCH_CART_ITEMS': {
               saveCartDataInLocalStorage(action.payload);
               return [...action.payload];
          }
          case 'INCREASE_PRODUCT_QUANTITY': {
             const newState = state.map(product => {
                  if (product.id == action.payload) {
                       product.quantity = parseInt(product.quantity) + 1;
                  }
                  return product;
             });

               saveCartDataInLocalStorage(newState);
               return newState;
          }
          case 'DECREASE_PRODUCT_QUANTITY' : {
               const newState = state.map(product => {
                    if (product.id == action.payload) {
                         product.quantity = parseInt(product.quantity) - 1;
                    }
                    return product;
               });

               saveCartDataInLocalStorage(newState);
               return newState;
          }
          case 'ADD_PRODUCT_IN_CART': {
               const newState = _.unionBy(state, [{...action.payload}], 'id');
               saveCartDataInLocalStorage(newState);
               return newState;
          }
          case 'REMOVE_PRODUCT_FROM_CART': {
               const newState = state.filter(item => item.id !== action.payload);
               saveCartDataInLocalStorage(newState);
               return newState;
          }
          default:
               return state;
     }
};


export const CartAppReducers = combineReducers({
     products: productReducers,
     cartItems: cartReducers
});
