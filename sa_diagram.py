from diagrams import Diagram, Cluster

from diagrams.onprem.queue import Kafka
from diagrams.onprem.analytics import Flink
from diagrams.elastic.elasticsearch import Kibana, Elasticsearch
from diagrams.onprem.database import PostgreSQL
from diagrams.onprem.network import Zookeeper
from diagrams.onprem.client import Client
from diagrams.onprem.container import Docker
from diagrams.c4 import Database

graph_attr = {
    "splines": "splines"
}

with Diagram("System Architecture", show=False, graph_attr=graph_attr):
    client = Client("Client Transaction")
    controller = Client("Control Center")

    with Cluster("Containerized applications", direction="TB"):
        kafka = Kafka("Kafka streaming")
        flink = Flink("Flink stream-processing") 
        # postgres = PostgreSQL("Data store")
        el_search = Elasticsearch("Elastissearch")
        kibana = Kibana("Data-Streaming Dashboard")
        zookeeper = Zookeeper("ZooKeeper")
        # docker = Docker("Docker")
    
    with Cluster("Postgres Database"):
        transactions = Database("transactions", description="Table")
        sales_per_category = Database("sales_per_category", description="Table")
        sales_per_day = Database("sales_per_day", description="Table")
        sales_per_month = Database("sales_per_month", description="Table")
    

    controller >> kafka
    client >> kafka >> zookeeper
    zookeeper>> kafka >> flink

    flink >> [
        transactions,
        sales_per_category,
        sales_per_day,
        sales_per_month,
    ]
    flink >> el_search  >> kibana