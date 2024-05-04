import os


def delete_doc_files(input_folder):
    for filename in os.listdir(input_folder):
        if filename.endswith(".doc"):
            doc_file_path = os.path.join(input_folder, filename)
            os.remove(doc_file_path)
            print(f"Deleted {filename}")


# Пример использования: удаление файлов .doc в папке 'docs' и оставление файлов .docx
delete_doc_files(r'C:\Users\admin\Desktop\diploma\docs')
