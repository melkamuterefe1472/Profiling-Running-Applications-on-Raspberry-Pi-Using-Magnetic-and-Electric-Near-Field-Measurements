package com.feedback.actions;

import com.feedback.dao.FeedbackDAO;
import com.feedback.dao.UserDAO;
import com.feedback.dao.ProductListDAO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.opensymphony.xwork2.ActionSupport;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import java.io.OutputStream;
import java.util.Map;
import java.util.List;

public class ExportReportAction extends ActionSupport {
    public String execute() {
        try {
            // Get servlet response
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=feedback_report.pdf");

            // DAOs
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            UserDAO userDAO = new UserDAO();
            ProductListDAO productDAO = new ProductListDAO();

            // Create PDF document
            Document document = new Document();
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("Product Feedback Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated on: " + new java.util.Date()));
            document.add(Chunk.NEWLINE);

            // Overview Stats
            int totalFeedback = feedbackDAO.getTotalFeedbackCount();
            int totalUsers = userDAO.getTotalUsers();
            int totalProducts = productDAO.getTotalProducts();
            double avgRating = feedbackDAO.getAverageRating();

            PdfPTable overview = new PdfPTable(2);
            overview.setWidthPercentage(100);
            overview.addCell("Total Feedbacks:");
            overview.addCell(String.valueOf(totalFeedback));
            overview.addCell("Registered Users:");
            overview.addCell(String.valueOf(totalUsers));
            overview.addCell("Total Products:");
            overview.addCell(String.valueOf(totalProducts));
            overview.addCell("Average Rating:");
            overview.addCell(String.format("%.2f / 5.0", avgRating));
            document.add(overview);

            document.add(Chunk.NEWLINE);

            // Pie chart placeholder (can be image later)
            document.add(new Paragraph("Feedback Status Distribution:"));
            Map<String, Integer> statusCounts = feedbackDAO.getFeedbackStatusCounts();
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                document.add(new Paragraph("• " + entry.getKey() + ": " + entry.getValue()));
            }

            document.add(Chunk.NEWLINE);

            // Table of top products (optional)
            document.add(new Paragraph("Top Feedbacked Products:"));
            
            PdfPTable productTable = new PdfPTable(3);
            productTable.setWidthPercentage(100);
            productTable.addCell("Product");
            productTable.addCell("Rating");
            productTable.addCell("Feedback Count");

            List<Map<String, Object>> topProducts = feedbackDAO.getTopFeedbackedProducts(); // you’ll need to add this
            for (Map<String, Object> row : topProducts) {
                productTable.addCell((String) row.get("name"));
                productTable.addCell(String.format("%.1f", row.get("avg_rating")));
                productTable.addCell(String.valueOf(row.get("count")));
            }

            document.add(productTable);

            // Close document
            document.close();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return NONE; // since response is direct
    }
}
