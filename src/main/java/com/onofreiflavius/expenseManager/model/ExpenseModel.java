package com.onofreiflavius.expenseManager.model;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "expenses")
public class ExpenseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String product;
    private Double price;
    private Double quantity;
    private String store;
    private String date;
    private Integer day;
    private Integer month;
    private Integer year;
    private Integer wasteful;
    private String user;

    public ExpenseModel() {}

    public ExpenseModel(String product, Double price, Double quantity, String store, String date, Integer day, Integer month, Integer wasteful, String user) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.store = store;
        this.date = date;
        this.day = day;
        this.month = month;
        this.wasteful = wasteful;
        this.user = user;

        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        Date currentYear = new Date();
        this.year = Integer.parseInt(sdf.format(currentYear));
    }

    // This constructor is for setting the default value of 'wasteful' to 0
    public ExpenseModel(String product, Double price, Double quantity, String store, String date, Integer day, Integer month, String user) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.store = store;
        this.date = date;
        this.day = day;
        this.month = month;
        this.wasteful = 0;
        this.user = user;

        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        Date currentYear = new Date();
        this.year = Integer.parseInt(sdf.format(currentYear));
    }

    // Id
    public Integer getId() {
        return id;
    }

    // Product
    public String getProduct() {
        return product;
    }
    public void setProduct(String newProduct) {
        product = newProduct;
    }

    // Price
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double newPrice) {
        price = newPrice;
    }

    // Quantity
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double newQuantity) {
        quantity = newQuantity;
    }

    // Store
    public String getStore() {
        return store;
    }
    public void setStore(String newStore) {
        store = newStore;
    }

    // Date
    public String getDate() {
        return date;
    }
    public void setDate(String newDate) {
        date = newDate;
    }

    // Day
    public Integer getDay() {
        return day;
    }
    public void setDay(Integer newDay) {
        day = newDay;
    }

    // Month
    public Integer getMonth() {
        return month;
    }
    public void setMonth(Integer newMonth) {
        month = newMonth;
    }

    // Year
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer newYear) {
        year = newYear;
    }

    // Wasteful
    public Integer getWasteful() {
        return wasteful;
    }
    public void setWasteful(Integer newWasteful) {
        wasteful = newWasteful;
    }

    // User
    public String getUser() { return user; }
    public void setUser(String newUser) { user = newUser; }

    // To String
    public String toString() { return product + " - " + price + " - " + quantity + " - " + store + " - " + day + " - " + month; }

}