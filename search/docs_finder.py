from elasticsearch import Elasticsearch
from flask import Flask, request, jsonify

# Создаем объект клиента Elasticsearch
es = Elasticsearch("http://localhost:9200", request_timeout=60)
app = Flask(__name__)

# # Индекс, в котором мы ищем документы
# index_name = "doc_index"
#
# # Запрос для поиска документа
# query = {
#     "query": {
#         "match": {
#             "text": "26 апреля 2024 года № 50-486"
#         }
#     }
# }
#
# try:
#     # Выполняем поиск документов
#     search_results = es.search(index=index_name, body=query)
#     # Обрабатываем результаты поиска
#     for hit in search_results['hits']['hits']:
#         print(hit['_source'])  # Выводим содержимое найденных документов
# except Exception as e:
#     print('ошибка при поиске документов:', e)


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


@app.route('/search', methods=['GET'])
def search_file():
    # id = request.form['id']
    # text = request.
    query = request.args.get('query')
    results = search_document(query)
    return jsonify(results)


if __name__ == '__main__':
    app.run(debug=True)
