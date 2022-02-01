import React from 'react';
import {CloseOutlined, GlobalOutlined, MinusOutlined, PlusOutlined,} from "@ant-design/icons";
import {Card, Input, InputNumber, Space} from "antd";
import {addProductInCart, decreaseProductQuantity, increaseProductQuantity, removeProductFromCart, updateProduct} from "../actions/CartAppActions";
import {connect} from "react-redux";

class Product extends React.Component {

    descQuantity = () => {
        if (this.props.product.quantity <= 1) {
            this.props.removeProductFromCart(this.props.product.id);
        } else {
            this.props.decreaseProductQuantity(this.props.product.id);
        }
    }

    incQuantity = () => {
        if (this.props.product.quantity <= 0) {
            this.props.addProductInCart({...this.props.product, quantity: 1});
        } else {
            this.props.increaseProductQuantity(this.props.product.id);
        }
    }

    render() {
        return (
            <Card hoverable="true">
                <div className="product-item">
                    <div className="product-description">
                        <div>
                            <Space>
                                <GlobalOutlined className="product-item-icon"/>
                            </Space>
                        </div>
                        <div className="item-details">
                            <div>{this.props.product.title}</div>
                            <div>{this.props.product.desc}</div>
                        </div>
                    </div>
                    <div className="product-item-price">
                        <div>
                            <Space>
                                <MinusOutlined onClick={this.descQuantity} className="product-quantity-icon"/>
                                <Input className="product-quantity" value={this.props.product.quantity} placeholder="0"/>
                                <PlusOutlined onClick={this.incQuantity} className="product-quantity-icon"/>
                            </Space>
                        </div>
                        <div className="product-price">{this.props.product.currency}{this.props.product.price}</div>
                    </div>
                </div>
            </Card>
        );
    }
}

const mapStateToProps = (state, ownProps) => {
    let product = state.products.find(prod => prod.id === ownProps.productId);
    if (product) {
        const inCart = state.cartItems.find(cartItem => cartItem.id === product.id);
        product = {...product, quantity: (inCart)? inCart.quantity: 0};
    }
    return {
        product
    };
}

export default connect(mapStateToProps, {removeProductFromCart,
    increaseProductQuantity, decreaseProductQuantity, addProductInCart})(Product);
