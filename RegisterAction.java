package com.feedback.actions;

import com.feedback.dao.UserDAO;
import com.feedback.model.User;
import com.opensymphony.xwork2.ActionSupport;

public class RegisterAction extends ActionSupport {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

    @Override
    public String execute() {
        
    	if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
    	    addActionError("Passwords do not match or are missing.");
    	    return INPUT;
    	}


        // ✅ Check if user already exists
        UserDAO dao = new UserDAO();
        if (dao.emailExists(email)) {
            addActionError("Email already registered.");
            return INPUT;
        }
        // ✅ Register new user
        User user = new User();
        user.setName(firstName + " " + lastName);
        user.setEmail(email);
        user.setPassword(password); // Password should ideally be hashed!
        user.setRole("user");
        boolean success = dao.registerUser(user);
        try {
        if (success) {
            addActionMessage("Registration successful!");
            return SUCCESS;
        } else {
            addActionError("Registration failed. Please try again.");
            return ERROR;
        }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ERROR; // This resolves to "error"
        }
    }

    // ======= Setters (required for Struts2 form binding) =======
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    // Optional Getters (useful if needed in result pages)
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}
