package saim.hassan.arfyppos.Model;

public class Favourites {
    private String ProductId,ProductName,ProductPrie,ProductMenuId,ProductImage,ProductDiscount,ProductDescription,UserPhone;

    public Favourites() {
    }

    public Favourites(String productId, String productName, String productPrie, String productMenuId, String productImage, String productDiscount, String productDescription, String userPhone) {
        ProductId = productId;
        ProductName = productName;
        ProductPrie = productPrie;
        ProductMenuId = productMenuId;
        ProductImage = productImage;
        ProductDiscount = productDiscount;
        ProductDescription = productDescription;
        UserPhone = userPhone;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrie() {
        return ProductPrie;
    }

    public void setProductPrie(String productPrie) {
        ProductPrie = productPrie;
    }

    public String getProductMenuId() {
        return ProductMenuId;
    }

    public void setProductMenuId(String productMenuId) {
        ProductMenuId = productMenuId;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductDiscount() {
        return ProductDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        ProductDiscount = productDiscount;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
