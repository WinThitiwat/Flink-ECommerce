# Flink E-Commerce Real-Time Streaming Pipeline
This is to simulate a situation of E-Commerce platform generating streaming data, such as sales transaction, and the real-time pipeline is here to support the data accessibility.

This project is to practice real-time data pipeline using Apache Kafka to stream data, Apache Flink to process the data in real-time manner, and sink the data into Postgres and Elastic Search to monitor the data output.


# System Architecture
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/system_architecture.png)

## Tech Stack
- `Apache Kafka`: to support pub/sub service.
- `Apache Flink`: to support data processing in real-time.
- `Postgres`: to store the transformed data from Flink for downstream usage.
- `Elastic Search`: to allow quick log search and analytics from the pipeline data.
- `Kibana`: to monitor the pipeline and visualize the executive summary report in real-time.

# Input Datasets
In this project, the Sales data is being generated using Faker Python library, and publish the data into Kafka topic, defined as `financial_transaction` topic.

Sample generated data from Kafka topic:
```
{"transactionId": "cf1f1500-3b03-4b8b-8de5-949fac7228e4", "productId": "product6", "productName": "laptop", "productCategory": "home", "productPrice": 45.98, "productQuantity": 7, "productBrand": "Oneplus", "currency": "USD", "customerId": "hullkristen", "transactionDate": "2024-05-24T07:20:45.281438", "paymentMethod": "credit_card", "totalAmount": 321.85999999999996}
```

# ETL Data Pipeline Process:
1. Make sure the pipeline already subscribed the `financial_transaction` topic to stream the data from Kafka broker.
2. Process the incoming streaming data using Apache Flink based on the business requirement.
3. Sink the data into different Postgres tables, and Elastic Search for data visualization as followed.

## Postgres Tables
### transactions
| transaction_id | product_id | product_name | product_category | product_price | product_quantity | product_brand | total_amount | currency | customer_id | transaction_date | payment_method | 
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | 
| "cf1f1500-3b03-4b8b-8de5-949fac7228e4" | "product6" | "laptop" | "home" | 45.98 | 7 | "Oneplus" | 321.85999999999996 | "USD" | "hullkristen" | "2024-05-24T07:20:45.281438" | "credit_card"| 

### sales_per_category
| transaction_date | product_category | total_sales |
| ---- | ---- | ---- | 
| "2024-05-24" | "home" | 321.85999999999996 |
### sales_per_day
| transaction_date |  total_sales |
| ---- | ---- | 
| "2024-05-24" | 321.85999999999996 |
### sales_per_month
| year | month | total_sales |
| ---- | ---- | ---- | 
| 2024 | 4 | 321.85999999999996 |

# Reference
## Flink Workflow
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/img/flink-workflow.png)
## Kibana Dashboard
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/img/kibana-dashboard.png)

** Note: Some part of the project codes might be found on the Internet as part of the learning and researching! **
