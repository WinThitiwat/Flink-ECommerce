package Dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Transaction {
//    {'transactionId': 'c56ba6d8-fa2c-446d-8a63-8ec24546afdc', 'productId': 'product6', 'productName': 'headphone',
//    'productCategory': 'electronic', 'productPrice': 134.28, 'productQuantity': 1, 'productBrand': 'Samsung',
//    'currency': 'GBP', 'customerId': 'donna19', 'transactionDate': '2024-05-03T06:30:35.563956', 'paymentMethod': 'debit_card', 'totalAmount': 134.28}
    private String transactionId;
    private String productId;
    private String productName;
    private String productCategory;
    private double productPrice;
    private int productQuantity;
    private String productBrand;
    private double totalAmount;
    private String currency;
    private String customerId;
    private Timestamp transactionDate;
    private String paymentMethod;
}
