from flask import Flask, request, jsonify
from index_docs import index_single_document
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


@app.route('/search', methods=['GET'])
def search_file():
    query = request.args.get('query')
    results = search_document(query)
    response = {"ids": []}
    for result in results:
        response["ids"].append(result["id"])

    return jsonify(response)
