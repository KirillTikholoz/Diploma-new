from elasticsearch import Elasticsearch

# Создаем объект клиента Elasticsearch
es = Elasticsearch("http://localhost:9200", request_timeout=60)

# Удаляем документ с идентификатором "1" из индекса "my_index"
res = es.delete(index="doc_index", id="20")
