package com.feedback.actions;

import com.feedback.dao.FeedbackListDAO;
import com.feedback.model.FeedbackList;
import com.opensymphony.xwork2.ActionSupport;

import java.util.List;

public class ViewAllFeedbackAction extends ActionSupport {

    private List<FeedbackList> feedbacks;

    @Override
    public String execute() {
        FeedbackListDAO dao = new FeedbackListDAO();
        feedbacks = dao.getAllFeedbacksWithDetails();
        return SUCCESS;
    }

    // Getter so that JSP can access the list
    public List<FeedbackList> getFeedbacks() {
        return feedbacks;
    }

    // Optional Setter (not necessary unless used in JSP form)
    public void setFeedbacks(List<FeedbackList> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
