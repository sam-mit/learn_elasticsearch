# learn_elasticsearch
learn elastic search


## initial days
....
I love gradle -- so import this as a gradle project!!

## learning from Baedlung Article
[Baeldung - Tutorial on Elasticsearch using Spring Data](https://www.baeldung.com/spring-data-elasticsearch-tutorial)


## Docker stuff for ES

[Elasticsearch on Docker](https://www.elastic.co/guide/en/elastic-stack-get-started/current/get-started-docker.html)

The Elasticsearch Dockercompose may run out of trial licence so instead we can try
```
docker run -d --name es771 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.7.1
```
