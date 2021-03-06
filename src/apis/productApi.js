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

export const getProducts = async () => {
    try {
        /*Can failed due to cross origin*/
        return await axiosHTTP('/products.json');
    } catch (e) {
       return {data: getSavedResponse()};
    }

}

export const getSavedResponse = () => {
    return {
        "products": [
            {
                "id": "123442",
                "title": "Product 1",
                "desc": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "image": "/product1.jpeg",
                "price": "39",
                "currency": "$"
            },
            {
                "id": "123443",
                "title": "Product 2",
                "desc": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                "image": "/product2.jpeg",
                "price": "39",
                "currency": "$"
            }
        ]
    };
}


