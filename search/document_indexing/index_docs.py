import os
import time
import pythoncom
from extract_text import extract_text_from_docx, extract_text_from_doc

from elasticsearch import Elasticsearch
from doc_to_docx import convert_doc_to_docx
import PyPDF2


index_name = 'doc_index'
es = Elasticsearch("http://localhost:9200", request_timeout=60)

# Настройки индекса
index_settings = {
    "settings": {
        "analysis": {
            "analyzer": {
                "default": {
                    "type": "standard",  # Используем стандартный токенизатор
                    "lowercase": True  # Приводим все буквы к нижнему регистру
                }
            }
        }
    }
}


# Функция для индексации документа в Elasticsearch
def index_document(index, doc_id, text):
    es.index(index=index, id=doc_id, body={'text': text})


def extract_name_from_path(path):
    last_slash_position = max(path.rfind('/'), path.rfind('\\'))
    if last_slash_position != -1:
        return path.removeprefix(path[:last_slash_position+1])
    else:
        return path


def index_single_document(document_path, doc_id):
    filename = extract_name_from_path(document_path)
    print(filename)

    if document_path.endswith(".docx"):
        try:
            text = extract_text_from_docx(document_path)
            print(text)
            try:
                print(doc_id, type(doc_id))
                index_document(index_name, doc_id, text)
                print('Документ успешно проиндексирован:', filename)
            except Exception as e:
                print('Ошибка при индексации документа:', e)
        except Exception as e:
            print('Ошибка при попытке извлечь текст:', e)

    elif filename.endswith(".doc"):
        print('Документ находится в старом формате и будет переведен в новый')
        docx_path = document_path[:-3] + 'docx'
        print(docx_path)
        try:
            pythoncom.CoInitialize()
            convert_doc_to_docx(document_path, docx_path)
            os.remove(document_path)
            index_single_document(docx_path, doc_id)
        except Exception as e:
            print('Ошибка при переводе документа в новый формат:', e)

    elif filename.endswith("pdf"):
        try:
            pdf_file_obj = open(document_path, 'rb')
            text = ""
            pdfreader = PyPDF2.PdfReader(pdf_file_obj)

            for i in range(0, len(pdfreader.pages)):
                page_obj = pdfreader.pages[i]
                text += page_obj.extract_text() + '\n'

            print(text)
            index_document(index_name, doc_id, text)
            print('Документ успешно проиндексирован:', filename)

        except Exception as e:
            print('Ошибка при индексации документа:', e)


def index_documents_from_folder(folder_path, index):
    for filename in os.listdir(folder_path):
        if filename.endswith(".docx"):
            file_path = os.path.join(folder_path, filename)
            try:
                text = extract_text_from_docx(file_path)
                print(text)
                try:
                    index_document(index, filename, text)
                    print('Документ успешно проиндексирован:', filename)
                    time.sleep(1)
                except Exception as e:
                    print('Ошибка при индексации документа:', e)
                    time.sleep(1)
            except Exception as e:
                print('Ошибка при попытке извлечь текст:', e)
                time.sleep(1)

        elif filename.endswith(".doc"):
            try:
                file_path = os.path.join(folder_path, filename)
                text = extract_text_from_doc(file_path)
                index_document(index, filename, text)
            except Exception as e:
                print('Формат doc:', e)
                time.sleep(1)
