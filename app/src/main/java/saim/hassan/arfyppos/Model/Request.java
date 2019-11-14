package saim.hassan.arfyppos.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String status;
    private String total;
    private String comments;
    private List<Order> products;

    public Request() {
    }

    public Request(String phone, String name, String address, String status, String total, String comments, List<Order> products) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.status = status;
        this.total = total;
        this.comments = comments;
        this.products = products;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<Order> getProducts() {
        return products;
    }

    public void setProducts(List<Order> products) {
        this.products = products;
    }
}
