from diagrams import Diagram, Cluster

from diagrams.onprem.queue import Kafka
from diagrams.onprem.analytics import Flink
from diagrams.elastic.elasticsearch import Kibana, Elasticsearch
from diagrams.onprem.database import PostgreSQL
from diagrams.onprem.network import Zookeeper
from diagrams.onprem.client import Client
from diagrams.onprem.container import Docker
graph_attr = {
    "splines": "splines"
}

with Diagram("System Architecture", show=False, graph_attr=graph_attr):
    client = Client("Client Transaction")
    controller = Client("Control Center")

    with Cluster("Containerized applications"):
        kafka = Kafka("Kafka streaming")
        flink = Flink("Flink stream-processing") 
        postgres = PostgreSQL("Data store")
        el_search = Elasticsearch("Elastissearch")
        kibana = Kibana("Data-Streaming Dashboard")
        zookeeper = Zookeeper("ZooKeeper")
        docker = Docker("Docker")
        
    
    controller >> kafka
    client >> kafka >> zookeeper
    zookeeper>> kafka >> flink

    flink >> postgres
    flink >> el_search  >> kibana