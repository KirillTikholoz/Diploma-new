from flask import Flask, request

app = Flask(__name__)


@app.route('/upload', methods=['POST'])
def upload_file():
    # print(request.form, '\n', request.files)
    # return 'кайф'
    # Получаем данные из формы
    file = request.files['file']
    id = request.form['id']

    # Обработка файла и id
    # Например, сохранение файла на сервере
    file.save(r'C:\Users\admin\Desktop\diploma\docs_finder\document_indexing' + file.filename)

    # Возвращаем сообщение об успешной загрузке
    return 'Файл успешно загружен. ID: {}'.format(id)


if __name__ == '__main__':
    app.run(debug=True)
