import React from 'react';
import {CaretDownOutlined, CloseOutlined, DownOutlined, ShoppingCartOutlined} from "@ant-design/icons";
import {Button, Divider, Dropdown, Menu, Popover, Space} from "antd";
import CartItem from "./CartItem";
import {connect} from "react-redux";
import {removeProductFromCart} from "../actions/CartAppActions";

class CartDetail extends React.Component {
    state = {
        visible: false,
    };
    handleVisibleChange = visible => {
        this.setState({visible});
    };

    getCartButtonText = () => {
        return (
            <span className="header-cart-action">
                <span>{`${this.props.totalItems} Items`}</span>
                <CaretDownOutlined className="header-cart-box-icon"/>
            </span>
        );
    }

    render() {
        return (
            <Popover
                content={this.renderCartList()}
                trigger="click"
                visible={this.state.visible}
                onVisibleChange={this.handleVisibleChange}
            >
                {this.getCartButtonText()}
            </Popover>
        );
    }

    renderCartList() {

        if (!this.props.cartItems || this.props.cartItems.length === 0) {
            return (
                <div style={{textAlign: "center"}}>Cart Is Empty</div>
            )
        }

        return this.props.cartItems.map(cartItem => {
            return (
                    <CartItem key={cartItem.id} removeItem={this.removeCartItem} cartItem={cartItem}/>
            );
        });
    }

    removeCartItem = productId => {
        this.props.removeProductFromCart(productId);
    }
}


const mapStateToProps = (state) => {
    return {
        cartItems: state.cartItems,
        totalItems: state.cartItems.reduce((acc, prod) => {
            return acc + prod.quantity;
        }, 0)
    }
}


export default connect(mapStateToProps, {removeProductFromCart})(CartDetail);
