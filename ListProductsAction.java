package com.feedback.actions;

import com.feedback.dao.ProductDAO;
import com.feedback.model.Product;
import com.opensymphony.xwork2.ActionSupport;

import java.util.List;

public class ListProductsAction extends ActionSupport {
    private List<Product> productList;

    public String execute() {
        productList = ProductDAO.getAllProducts();
        System.out.println(productList);
        return SUCCESS;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
