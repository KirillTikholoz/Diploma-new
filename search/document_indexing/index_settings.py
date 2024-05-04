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
