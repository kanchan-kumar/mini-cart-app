import React from 'react';
import Product from "./Product";
import {connect} from "react-redux";
import {fetchProducts} from "../actions/CartAppActions";

class ProductDetail extends React.Component {

    componentDidMount() {
        this.props.fetchProducts();
    }

    render() {
        return (
            <div className="product-detail">
                {this.getAllProductItems()}
            </div>
        );
    }

    getAllProductItems = () => {
        return this.props.products.map(product => {
                return (
                    <Product productId={product.id} key={product.id}/>
                );
            }
        );
    }
}

const mapStateToProps = (state) => {
    return {
        products: state.products
    }
}

export default connect(mapStateToProps, {fetchProducts})(ProductDetail);
