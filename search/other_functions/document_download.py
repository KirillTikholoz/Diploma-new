import requests
from bs4 import BeautifulSoup

# URL страницы, с которой вы хотите скачать документы
base_url = 'https://saratovduma.ru/decision/perechen-resheniy-6-sozyva.php'

# Отправка запроса к странице
response = requests.get(base_url)

# Проверка успешности запроса
if response.status_code == 200:
    # Используем BeautifulSoup для парсинга HTML
    soup = BeautifulSoup(response.text, 'html.parser')

    # Находим все ссылки на странице
    links = soup.find_all('a', href=True)

    # for link in links:
    #     href = 'https://saratovduma.ru'+link.get('href')
    #     print(href)

    # Перебираем все найденные ссылки
    for link in links:
        href = 'https://saratovduma.ru' + link.get('href')
        print(href)
        # Проверяем, что ссылка ведет на файл .doc
        if href.endswith('.doc'):
            # Формируем полный URL для скачивания
            doc_url = href
            print(doc_url)

            # Получаем имя файла из ссылки
            filename = href.split('/')[-1]

            # Указываем полный путь к файлу
            save_path = 'C:/Users/admin/Desktop/diploma/' + filename

            # Скачиваем файл, указывая путь сохранения
            response_doc = requests.get(doc_url)

            # Проверяем тип содержимого
            content_type = response_doc.headers['Content-Type']
            if 'application/msword' in content_type:
                with open(save_path, 'wb') as f:
                    f.write(response_doc.content)
                print(f'Файл {filename} успешно скачан и сохранён в {save_path}.')
            else:
                print(f'Ссылка {doc_url} не ведёт к файлу .doc.')
else:
    print('Ошибка при загрузке страницы:', response.status_code)
