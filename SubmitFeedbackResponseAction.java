package com.feedback.actions;

import com.feedback.dao.FeedbackListDAO;
import com.opensymphony.xwork2.ActionSupport;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import java.io.PrintWriter;

public class SubmitFeedbackResponseAction extends ActionSupport {
    private String feedback_id;
    private String response;
    private String status;

    @Override
    public String execute() throws Exception {
        HttpServletResponse httpResponse = ServletActionContext.getResponse();
        httpResponse.setContentType("application/json");
        PrintWriter out = httpResponse.getWriter();

        try {
            FeedbackListDAO dao = new FeedbackListDAO();
            boolean success = dao.updateAdminResponse(feedback_id, response, status);

            if (success) {
                out.print("{\"status\": \"success\"}");
            } else {
                out.print("{\"status\": \"error\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        }
        out.flush();
        return null; // Avoid Struts result mapping
    }

    // Setters for fields from AJAX
    public void setFeedback_id(String feedback_id) {
        this.feedback_id = feedback_id;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
