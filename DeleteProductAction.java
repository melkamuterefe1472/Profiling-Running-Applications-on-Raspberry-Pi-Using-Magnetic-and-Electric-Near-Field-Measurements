package com.feedback.actions;

import com.feedback.dao.ProductDAO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteProductAction extends ActionSupport {
    private int id;

    public String execute() {
        boolean deleted = ProductDAO.deleteProduct(id);
        return deleted ? SUCCESS : ERROR;
    }

    public void setId(int id) {
        this.id = id;
    }
}
