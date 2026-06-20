package com.feedback.actions;

import com.feedback.dao.ProductDAO;
import com.feedback.model.Product;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddProductAction extends ActionSupport {

    // ==== Form Fields ====
    private String productName;
    private String productSKU;
    private String productCategory;
    private String productBrand;
    private String productDescription;
    private String productSpecifications;


    private File productThumbnail;
    private String productThumbnailFileName;

    private List<File> productGallery;
    private List<String> productGalleryFileName;

    @Override
    public String execute() {
        try {
            // Get the absolute path to webapp/uploads
            String uploadPath = ServletActionContext.getServletContext().getRealPath("/uploads");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // === Save Thumbnail ===
            File thumbnailDest = new File(uploadDir, productThumbnailFileName);
            FileUtils.copyFile(productThumbnail, thumbnailDest);
            if (productThumbnail != null && productThumbnailFileName != null) {
                System.out.println("✅ Thumbnail received: " + productThumbnailFileName);
            } else {
                System.out.println("❌ Thumbnail is missing or not uploaded.");
            }

            // === Create Product Object ===
            Product product = new Product();
            product.setName(productName);
            product.setSku(productSKU);
            product.setCategory(productCategory);
            product.setBrand(productBrand);
            product.setDescription(productDescription);
            product.setThumbnail("uploads/" + productThumbnailFileName); // relative path
            product.setSpecifications(productSpecifications);

            // === Save to DB and get product ID ===
            int productId = ProductDAO.saveProduct(product);
            product.setStatus("Active"); // default value

            // === Save Gallery Images ===
            List<String> galleryPaths = new ArrayList<>();
            if (productGallery != null && productGalleryFileName != null) {
                for (int i = 0; i < productGallery.size(); i++) {
                    File image = productGallery.get(i);
                    String fileName = productGalleryFileName.get(i);
                    File dest = new File(uploadDir, fileName);
                    FileUtils.copyFile(image, dest);
                    galleryPaths.add("uploads/" + fileName);
                }
                ProductDAO.saveGalleryImages(productId, galleryPaths);
            }
            System.out.println("Saving thumbnail to: " + thumbnailDest.getAbsolutePath());

            addActionMessage("Product added successfully!");
            return SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            addActionError("Failed to add product.");
            return ERROR;
        }
    }

    // ==== Getters and Setters ====

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSKU() {
        return productSKU;
    }
    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }

    public String getProductCategory() {
        return productCategory;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductBrand() {
        return productBrand;
    }
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductDescription() {
        return productDescription;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public String getProductSpecifications() {
        return productSpecifications;
    }

    public void setProductSpecifications(String productSpecifications) {
        this.productSpecifications = productSpecifications;
    }

    public File getProductThumbnail() {
        return productThumbnail;
    }
    public void setProductThumbnail(File productThumbnail) {
        this.productThumbnail = productThumbnail;
    }

    public String getProductThumbnailFileName() {
        return productThumbnailFileName;
    }
    public void setProductThumbnailFileName(String productThumbnailFileName) {
        this.productThumbnailFileName = productThumbnailFileName;
    }

    public List<File> getProductGallery() {
        return productGallery;
    }
    public void setProductGallery(List<File> productGallery) {
        this.productGallery = productGallery;
    }

    public List<String> getProductGalleryFileName() {
        return productGalleryFileName;
    }
    public void setProductGalleryFileName(List<String> productGalleryFileName) {
        this.productGalleryFileName = productGalleryFileName;
    }
}
