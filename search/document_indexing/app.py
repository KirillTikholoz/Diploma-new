from flask import Flask, request, jsonify
from index_docs import index_single_document, replace_document
from docs_finder import search_document

app = Flask(__name__)


@app.route('/upload', methods=['POST'])
def upload_file():
    file = request.files['file']
    doc_id = int(request.form['id'])
    print(doc_id)
    filepath = f'C:\\Users\\admin\\Desktop\\diploma\\docs\\{file.filename}'
    print(filepath)
    file.save(filepath)
    index_single_document(filepath, doc_id)
    return f'Файл успешно загружен и добавлен в поисковик. ID:'.format(doc_id)


@app.route('/search/document', methods=['GET'])
def search_file():
    query = request.args.get('query')
    results = search_document(query)
    response = {"ids": []}
    for result in results:
        response["ids"].append(result["id"])

    return jsonify(response)


@app.route('/search/project', methods=['GET'])
def search_file():
    query = request.args.get('query')
    results = search_document(query)
    response = {"ids": []}
    for result in results:
        response["ids"].append(result["id"])

    return jsonify(response)


@app.route('/search/solution', methods=['GET'])
def search_file():
    query = request.args.get('query')
    results = search_document(query)
    response = {"ids": []}
    for result in results:
        response["ids"].append(result["id"])

    return jsonify(response)


@app.route('/project_to_decision', methods=['POST'])
def project_to_decision():
    request_data = request.json()
    document_id = request_data.get("documentId")
    replace_document(document_id)
    return f'Проект успешно проведен в решения:'.format(document_id)
