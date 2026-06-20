package com.feedback.actions;

import com.feedback.dao.ProductDAO;
import com.feedback.model.Product;
import com.opensymphony.xwork2.ActionSupport;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import java.io.PrintWriter;

public class GetProductDetailAction extends ActionSupport {
    private int id;

    public String execute() {
        try {
            Product product = ProductDAO.getProductById(id);
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(new Gson().toJson(product));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setId(int id) {
        this.id = id;
    }
}
