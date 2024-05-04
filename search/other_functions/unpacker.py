# import os
# import zipfile
#
# def extract_all_archives(folder_path):
#     for filename in os.listdir(folder_path):
#         if filename.endswith('.zip'):
#             archive_path = os.path.join(folder_path, filename)
#             with zipfile.ZipFile(archive_path, 'r') as zip_ref:
#                 zip_ref.extractall(folder_path)
#                 print(f"Архив {filename} был успешно распакован.")
#
# if __name__ == "__main__":
#     folder_path = 'C:/Users/admin/Desktop/diploma'  # Укажите путь к вашей папке
#     extract_all_archives(folder_path)

import os
import shutil


def move_files_from_subfolders(folder_path, destination_folder):
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            destination_path = os.path.join(destination_folder, file)
            if not os.path.exists(destination_path):
                shutil.move(file_path, destination_folder)
                print(f"Файл {file} был перемещен в {destination_folder}")
            else:
                print(f"Файл {file} уже существует в {destination_folder}. Пропускаем перемещение.")


if __name__ == "__main__":
    source_folder = 'C:/Users/admin/Desktop/diploma'  # Исходная папка
    destination_folder = 'C:/Users/admin/Desktop/diploma'  # Папка, в которую нужно переместить файлы
    move_files_from_subfolders(source_folder, destination_folder)

