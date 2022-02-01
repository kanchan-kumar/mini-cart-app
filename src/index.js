import React from 'react';
import ReactDOM from 'react-dom';
import "antd/dist/antd.css";
import './index.css';
import CartApp from "./components/CartApp";
import {CartAppReducers} from "./reducers/CartAppReducers";
import {applyMiddleware, createStore} from "redux";
import thunk from "redux-thunk";
import {Provider} from "react-redux";

const store = createStore(CartAppReducers, applyMiddleware(thunk));
ReactDOM.render(
    <Provider store={store}>
        <React.StrictMode>
        <CartApp/>
        </React.StrictMode>,
    </Provider>,
    document.querySelector("#root"));


if (module.hot) {
    module.hot.accept();
}
