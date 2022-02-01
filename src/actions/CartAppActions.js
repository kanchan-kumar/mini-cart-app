import {axiosHTTP, fetchHTTP, getCartItems} from "../apis/productApi";

export const fetchProducts = () => {
    return async (dispatch, getState) => {
        const response = await axiosHTTP('/products.json');

        /*Product default state have 1 quantity.*/
        const products = response.data.products.map(item => {
            item.quantity = 1;
            return item;
        });
        dispatch({
            type: 'FETCH_PRODUCTS',
            payload: products
        });
    }
};

export const fetchCartItems = () => {
    return async (dispatch, getState) => {

        let cartItems = await getCartItems();

        if (! cartItems) {
            const response = await axiosHTTP('/products.json');
            cartItems = response.data.products;
            cartItems = cartItems.map(item => {
                item.quantity = 1;
                return item;
            });
        }
        dispatch({
            type: 'FETCH_CART_ITEMS',
            payload: cartItems
        });
    }
};


export const addProductInCart = product => {
    return {
        type: 'ADD_PRODUCT_IN_CART',
        payload: product
    };
};

export const removeProductFromCart = productId => {
    return {
        type: 'REMOVE_PRODUCT_FROM_CART',
        payload: productId
    };
};

export const increaseProductQuantity = productId => {
    return {
        type: 'INCREASE_PRODUCT_QUANTITY',
        payload: productId
    };
};


export const decreaseProductQuantity = productId => {
    return {
        type: 'DECREASE_PRODUCT_QUANTITY',
        payload: productId
    };
};

export const updateProduct = product => {
    return {
        type: 'UPDATE_PRODUCT',
        payload: product
    };
}

