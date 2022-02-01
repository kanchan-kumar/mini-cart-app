import React from 'react';
import Header from "./Header";
import ProductDetail from "./ProductDetail";

const CartApp = () => {
    return (
        <div className="cart-app">
            <Header/>
            <ProductDetail/>
        </div>
    );
};

export default CartApp;
