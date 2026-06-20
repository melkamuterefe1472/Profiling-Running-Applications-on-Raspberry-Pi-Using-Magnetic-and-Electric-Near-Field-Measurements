package com.feedback.actions;

import com.feedback.dao.FeedbackListDAO;
import com.feedback.model.FeedbackList;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class GetFeedbackDetailAction extends ActionSupport implements ServletResponseAware {
    private String id;
    private HttpServletResponse response;

    @Override
    public String execute() {
        try {
            FeedbackListDAO dao = new FeedbackListDAO();
            FeedbackList fb = dao.getFeedbackById(id);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();

            if (fb == null) {
                out.print("{\"error\": \"Feedback not found\"}");
            } else {
                out.print(gson.toJson(fb));
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
//This method receives the HTTP response object from Struts2 and stores it in your action class.