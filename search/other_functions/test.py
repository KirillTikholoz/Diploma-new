import os
import textract
from elasticsearch import Elasticsearch
from docx import Document


# Подключаемся к Elasticsearch
# es = Elasticsearch([{'host': 'localhost', 'port': 9200}])

# Функция для извлечения текста из документа .docx, включая таблицы
def extract_text_from_docx(file_path):
    doc = Document(file_path)
    text = ''
    for paragraph in doc.paragraphs:
        text += paragraph.text + '\n'
    for table in doc.tables:
        for row in table.rows:
            for cell in row.cells:
                text += cell.text + '\t'  # Добавляем табуляцию между ячейками таблицы
            text += '\n'
    return text.strip()


# Функция для извлечения текста из документа .doc
def extract_text_from_doc(file_path):
    text = textract.process(file_path).decode('utf-8')
    return text.strip()


# Функция для индексации документа в Elasticsearch
# def index_document(index_name, doc_id, text):
#     es.index(index=index_name, id=doc_id, body={'text': text})
#
# # Основная функция для индексации всех документов в заданной папке
# def index_documents_from_folder(folder_path, index_name):
#     for filename in os.listdir(folder_path):
#         if filename.endswith(".docx"):
#             file_path = os.path.join(folder_path, filename)
#             text = extract_text_from_docx(file_path)
#             index_document(index_name, filename, text)
#         elif filename.endswith(".doc"):
#             file_path = os.path.join(folder_path, filename)
#             text = extract_text_from_doc(file_path)
#             index_document(index_name, filename, text)

# Пример использования: индексация документов из папки 'docs' в индекс 'my_index'
# index_documents_from_folder('docs', 'my_index')
folder_path = r'C:\Users\admin\Desktop\diploma\docs'
for filename in os.listdir(folder_path):
    file_path = os.path.join(folder_path, filename)
    print(file_path)
    text = extract_text_from_docx(file_path)
    print(text)
