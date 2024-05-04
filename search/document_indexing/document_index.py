import os
import time
import index_settings
from fastapi import FastAPI
import textract
from elasticsearch import Elasticsearch
from docx import Document

# Подключаемся к Elasticsearch
es = Elasticsearch("http://localhost:9200", request_timeout=60)

def document_index(filename):
    if 
