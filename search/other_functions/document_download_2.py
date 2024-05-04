import requests
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By

# Путь к веб-драйверу
driver_path = 'C:/Users/admin/Downloads/chromedriver-win64122stable/chromedriver-win64/chromedriver.exe'
service = Service(driver_path)

# Создание экземпляра драйвера Chrome
driver = webdriver.Chrome(service=service)

# URL страницы, с которой вы хотите скачать документы
base_url = 'https://saratovduma.ru/decision/perechen-resheniy-6-sozyva.php'

# Открытие страницы в браузере
driver.get(base_url)

# Выполнение JavaScript кода для получения нужного элемента (например, по XPath)
element = driver.find_element(By.XPATH, '/html/body/table/tbody/tr[2]/td/table/tbody/tr/td[1]/table/tbody/tr[1]/td')

# Нахождение всех ссылок внутри этого элемента
links = element.find_elements(By.TAG_NAME, 'a')

# Перебор всех найденных ссылок
for link in links:
    href = link.get_attribute('href')
    print(href)  # Выводим ссылки на экран
    # Отправка запроса к странице
    response = requests.get(href)
    # Проверка успешности запроса
    if response.status_code == 200:
        # Используем BeautifulSoup для парсинга HTML
        soup = BeautifulSoup(response.text, 'html.parser')
        # Находим все ссылки на странице
        links2 = soup.find_all('a', href=True)
        for link2 in links2:
            href2 = link2.get('href')
            print(href2)
            if ('https' not in href2) and ('http' not in href2):
                href2 = 'https://saratovduma.ru' + link2.get('href')
            print(href2)

            # Проверяем, что ссылка ведет на файл .doc, .zip или .rar
            if href2.endswith(('.doc', '.zip', '.rar')):
                # Формируем полный URL для скачивания
                doc_url = href2
                # Получаем имя файла из ссылки
                filename = href2.split('/')[-1]
                # Указываем полный путь к файлу
                save_path = 'C:/Users/admin/Desktop/diploma/' + filename
                # Скачиваем файл, указывая путь сохранения
                response_doc = requests.get(doc_url)
                # Проверяем тип содержимого
                content_type = response_doc.headers['Content-Type']
                if 'application/msword' in content_type or 'application/zip' in content_type or 'application/rar' in content_type:
                    with open(save_path, 'wb') as f:
                        f.write(response_doc.content)
                    print(f'Файл {filename} успешно скачан и сохранён в {save_path}.')
                else:
                    print(f'Ссылка {doc_url} не ведёт к поддерживаемому файлу.')
        else:
            print('Ошибка при загрузке страницы:', response.status_code)

# Закрытие браузера
driver.quit()
