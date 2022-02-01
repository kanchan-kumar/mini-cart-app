import React from 'react';
import {PageHeader, Space} from 'antd';
import {ShoppingCartOutlined} from '@ant-design/icons';
import CartDetail from "./CartDetail";
import {connect} from "react-redux";
import {fetchCartItems, fetchProducts} from "../actions/CartAppActions";

class Header extends React.Component {

    componentDidMount() {
        this.props.fetchCartItems();
    }

    render() {
        return (
            <div className="cart-app-header">
                <PageHeader
                    extra={[
                        <div className="header-cart-items">
                            <div>${this.props.totalItemCost}</div>
                            <div><CartDetail/></div>
                        </div>,
                        <Space>
                            <ShoppingCartOutlined className="header-cart-icon"/>
                        </Space>,
                    ]}
                >
                </PageHeader>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        totalItemCost: state.cartItems.reduce((acc, item) => {
            return acc + (parseInt(item.price) * item.quantity);
        }, 0)
    }
}

export default connect(mapStateToProps, {fetchCartItems})(Header);
