kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic hello-producer-1 --config min.insync.replicas=2
