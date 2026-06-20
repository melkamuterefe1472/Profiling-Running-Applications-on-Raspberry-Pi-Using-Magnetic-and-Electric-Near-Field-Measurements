package com.feedback.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class LogoutAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;

    @Override
    public String execute() {
        if (session != null) {
            session.clear(); // ✅ This logs out the user
        }
        return SUCCESS; // Forward to login.jsp
    }
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
