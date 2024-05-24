# [WIP] Summary
This project is to practice real-time data pipeline using Kafka to stream data, Flink to process the data, and sink the data into Postgres and Elastic Search to monitor the data output.


# System Architecture
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/system_architecture.png)

# Datasets
In this project, the Sales data is being generated using Faker Python library, and publish the data into Kafka topic, defined as `financial_transaction` topic.

Sample generated data:
```
{"transactionId": "cf1f1500-3b03-4b8b-8de5-949fac7228e4", "productId": "product6", "productName": "laptop", "productCategory": "home", "productPrice": 45.98, "productQuantity": 7, "productBrand": "Oneplus", "currency": "USD", "customerId": "hullkristen", "transactionDate": "2024-05-24T07:20:45.281438", "paymentMethod": "credit_card", "totalAmount": 321.85999999999996}
```

# Reference
## Flink Workflow
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/img/flink-workflow.png)
## Kibana Dashboard
![alt text](https://github.com/WinThitiwat/Flink-ECommerce/blob/main/img/kibana-dashboard.png)
