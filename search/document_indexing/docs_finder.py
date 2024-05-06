from elasticsearch import Elasticsearch


es = Elasticsearch("http://localhost:9200", request_timeout=60)


def search_document(message, search_index='doc_index'):

    search_body = {
        "query": {
            "match": {
                "text": message
            }
        }
    }

    try:
        search_result = es.search(index=search_index, body=search_body)
        results = []
        for hit in search_result['hits']['hits']:
            # только ID документа в результаты
            result = {"id": hit['_id']}
            results.append(result)
        return results
    except Exception as e:
        print('ошибка при поиске документов:', e)
        return []
