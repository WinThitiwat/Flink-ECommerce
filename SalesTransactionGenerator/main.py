import random
import time
import json

from faker import Faker
from confluent_kafka import SerializingProducer
from datetime import datetime

fake = Faker()

def gen_sales_transaction():
    """
    Generate of fake sales transaction information
    :return: A dictionary of fake transaction
    """
    user = fake.simple_profile()

    return {
        "transactionId": fake.uuid4(),
        "productId": random.choice([f"product{num}" for num in range(1,7)]),
        "productName": random.choice(["laptop", "mobile", "tablet", "watch", "headphone", "speaker"]),
        "productCategory": random.choice(["electronic", "fashion", "grocery", "home", "beauty", "sports"]),
        "productPrice": round(random.uniform(10, 1000), 2),
        "productQuantity": random.randint(1, 10),
        "productBrand": random.choice(["Apple", "Samsung", "Oneplus", "XiaoMi", "Boat", "Sony"]),
        "currency": random.choice(["USD", "GBP"]),
        "customerId": user["username"],
        "transactionDate": datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S.%f%z"),
        "paymentMethod": random.choice(["credit_card", "debit_card", "online_transfer"])
    }

def delivery_report(err, msg):
    if err is not None:
        print(f"Message delivery failed: {err}")
    else:
        print(f"Message delivered to {msg.topic} [{msg.partition()}]")

def write_to_kafka(kafka_producer: SerializingProducer, topic: str, transaction: dict):
    kafka_producer.produce(
        topic = topic,
        key = transaction["transactionId"],
        value = json.dumps(transaction),
        on_delivery = delivery_report
        )
    kafka_producer.poll(0)

def main():
    topic = "financial_transaction"
    producer = SerializingProducer({
        "bootstrap.servers": "localhost:9092"
    })

    curr_time = datetime.now()
    while ( datetime.now() - curr_time ).seconds < 120:
        try:
            transaction = gen_sales_transaction()
            transaction["totalAmount"] = transaction["productPrice"] * transaction["productQuantity"]
            print(transaction)
            
            write_to_kafka(producer, topic, transaction)

            # wait 5 seconds for test
            time.sleep(5)
        
        except BufferError:
            print("Buffer full! Waiting...")

        except Exception as e:
            print(f"Error: {e}")

if __name__ == "__main__":
    main()