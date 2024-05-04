import os


def count_doc_files(folder_path):
    count_doc = 0
    count_rar = 0
    for filename in os.listdir(folder_path):
        if filename.endswith(".docx"):
            count_doc += 1
        if filename.endswith(".rar"):
            count_rar += 1
    return count_doc, count_rar


if __name__ == "__main__":
    folder_path = 'C:/Users/admin/Desktop/diploma/docs'
    doc_count, rar_count = count_doc_files(folder_path)
    print(f"Количество файлов формата .doc в папке {folder_path}: {doc_count}")
    print(f"Количество файлов формата .zip в папке {folder_path}: {rar_count}")
