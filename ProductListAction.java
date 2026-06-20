package com.feedback.actions;

import com.feedback.dao.ProductListDAO;
import com.feedback.model.ProductList;
import com.opensymphony.xwork2.ActionSupport;

import java.util.List;

public class ProductListAction extends ActionSupport {

    private List<ProductList> products;

    public String execute() {
//    	 System.out.println(">>> Inside ProductListAction execute()");
         ProductListDAO dao = new ProductListDAO();
         products = dao.getAllProducts();
//         System.out.println(">>> Loaded " + products.size() + " products.");
         return SUCCESS;
    }

    public List<ProductList> getProducts() {
        return products;
    }

    public void setProducts(List<ProductList> products) {
        this.products = products;
    }
}
