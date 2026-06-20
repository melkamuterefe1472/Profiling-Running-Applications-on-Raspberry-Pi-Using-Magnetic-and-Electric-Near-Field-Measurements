package com.feedback.actions;

import com.feedback.dao.FeedbackDAO;
import com.feedback.model.Feedback;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class SubmitFeedbackAction extends ActionSupport implements SessionAware {
    private int productId;
    private int rating;
    private String comments;
    private String email;
    private String allowContact;

    private Map<String, Object> session;
    @Override
    public String execute() throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            Feedback feedback = new Feedback();
            feedback.setId(new FeedbackDAO().generateFeedbackId());
            feedback.setProductId(productId);
            feedback.setRating(rating);
            feedback.setComments(comments);
            feedback.setStatus("Pending");
            feedback.setEmail(email);
            feedback.setAllowContact("on".equalsIgnoreCase(allowContact) || "true".equalsIgnoreCase(allowContact));

            Integer userId = (Integer) session.get("userId");
            feedback.setUserId(userId != null ? userId : 0);

            boolean saved = new FeedbackDAO().saveFeedback(feedback);

            out.print("{\"status\":\"" + (saved ? "success" : "error") + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        } finally {
            out.flush();
            out.close();
        }

        return "none"; // ✅ Return a valid result to avoid 404
    }

    // Setters for form data
    public void setProductId(int productId) { this.productId = productId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComments(String comments) { this.comments = comments; }
    public void setEmail(String email) { this.email = email; }
    public void setAllowContact(String allowContact) { this.allowContact = allowContact; }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
