import React from 'react';
import {CloseOutlined, } from "@ant-design/icons";
import {Space} from "antd";

class CartItem extends React.Component {

    render() {
        return (
            <div className="cart-item">
                <div className="cart-product-description">
                    <div>
                        <Space>
                            <CloseOutlined onClick={() => this.props.removeItem(this.props.cartItem.id)} className="cart-item-icon"/>
                        </Space>
                    </div>
                    <div className="cart-items">
                        <div>{this.props.cartItem.title}</div>
                        <div>{this.props.cartItem.currency}{this.props.cartItem.price}</div>
                    </div>
                </div>
                <div className="cart-product-detail">
                    <div>Qty</div>
                    <div>{this.props.cartItem.quantity}</div>
                </div>
            </div>
        );
    }
}

export default CartItem;
