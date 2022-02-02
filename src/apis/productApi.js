import axios from "axios";

export const BASE_URL = 'https://dnc0cmt2n557n.cloudfront.net';
export const PROXY_FOR_CORS_DEV_ONLY = "";

export const axiosHTTP = axios.create(
    {
        baseURL: PROXY_FOR_CORS_DEV_ONLY + BASE_URL,
        headers: {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET,OPTIONS',
            'Content-Type': 'application/json',
        },
        mode: 'no-cors',
        responseType: "json",
    }
);


export const getCartItems = async () => {
    try {
        return (localStorage.getItem('CART_ITEMS') ? JSON.parse(localStorage.getItem('CART_ITEMS')): null);
    } catch (e) {
        return [];
    }
}

export const saveCartDataInLocalStorage = (cartItems) => {
    if (cartItems && cartItems.length > 0) {
        localStorage.setItem('CART_ITEMS', JSON.stringify(cartItems));
    } else {
        localStorage.removeItem("CART_ITEMS");
    }
}
