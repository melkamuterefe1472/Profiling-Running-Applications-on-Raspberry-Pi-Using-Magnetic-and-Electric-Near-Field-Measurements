package com.feedback.actions;
import com.google.gson.Gson;
import com.feedback.dao.FeedbackDAO;
import com.feedback.dao.ProductListDAO;
import com.feedback.dao.UserDAO;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginAction extends ActionSupport {
    private String email;
    private String password;
 	private List<String> chartLabels;
	private List<Integer> chartData;
	public String execute() {
	    if (UserDAO.validateUser(email, password)) {
	        int userId = UserDAO.getUserIdByEmail(email);
	        String role = UserDAO.getUserRole(email);

	        Map<String, Object> session = ActionContext.getContext().getSession();
	        session.put("user", email);
	        session.put("role", role);
	        session.put("userId", userId);

	        // feedback chart data for dashboard
	        if ("admin".equalsIgnoreCase(role)) {
	            FeedbackDAO feedbackDAO = new FeedbackDAO();
	            UserDAO userDAO = new UserDAO();
	            ProductListDAO productDAO = new ProductListDAO();
	            // Chart Data
	            Map<String, Integer> chartData = feedbackDAO.getMonthlyFeedbackCounts();
	            Map<String, Integer> statusCounts = feedbackDAO.getFeedbackStatusCounts();
	            Gson gson = new Gson();
	            session.put("chartLabels", gson.toJson(chartData.keySet()));
	            session.put("chartValues", gson.toJson(chartData.values()));
	            session.put("pieLabels", gson.toJson(statusCounts.keySet()));
	            session.put("pieValues", gson.toJson(statusCounts.values()));

	            // Stats
	            session.put("feedbackCount", feedbackDAO.getTotalFeedbackCount());
	            session.put("avgRating", feedbackDAO.getAverageRating());
	            session.put("userCount", userDAO.getTotalUsers());
	            session.put("productCount", productDAO.getTotalProducts());

	            return "admin";
	        }
	        else {
	            return "user";
	        }
	    } else {
	        addActionError("Invalid email or password");
	        return ERROR;
	    }
	}

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<String> getChartLabels() { return chartLabels; }
    public List<Integer> getChartData() { return chartData; }
}
