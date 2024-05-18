import comtypes.client as cc


# Функция для преобразования файла .doc в .docx
def convert_doc_to_docx(doc_file_path, docx_file_path):
    word = cc.CreateObject("Word.Application")
    doc = word.Documents.Open(doc_file_path)
    print('файл открыт')
    doc.SaveAs(docx_file_path, FileFormat=16)
    doc.Close()
    word.Quit()
