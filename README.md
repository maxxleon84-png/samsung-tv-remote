# Samsung TV Remote

Android-пульт для старых Samsung Smart TV (H-серия 2014–2015, Orsay OS) по сетевому протоколу порта 55000.

Написан потому, что мой оригинальный Smart Touch Remote умер, а в Play Market все альтернативы платные. Параллельно — кейс для канала [@my_way_in_wibecoding](https://t.me/my_way_in_wibecoding).

## Поддерживаемые модели

Проверено на: **Samsung UE40H6410AU** (2015).
Должно работать на других Samsung H-серии 2014–2015 с сетевым управлением.

## Установка

1. Открой [Releases](https://github.com/maxxleon84-png/samsung-tv-remote/releases)
2. Скачай последний `samsung-remote-vX.X.X.apk` на телефон
3. Один раз разреши установку из неизвестных источников для своего браузера
4. Тап по APK → «Установить»

## Первый запуск

1. Введи IP телевизора (Меню → Сеть → Состояние сети → IP-адрес)
2. Нажми «Подключить»
3. На ТВ всплывёт запрос «Разрешить подключение?» — нажми **Разрешить** штатным пультом или кнопками на корпусе ТВ
4. Готово, можно управлять

## Что умеет

- Power, Source, Mute
- Громкость ±, каналы ±
- D-pad (стрелки + OK)
- Back, Home, Exit
- Цифры 0–9

## Что не умеет

- Эмуляцию тачпада Smart Touch Remote (невозможно в принципе — ТВ слушает только дискретные команды)
- Авто-поиск ТВ в сети (в планах на v1.1)
- Управление несколькими телевизорами
- Работу вне локальной Wi-Fi

## Стек

Kotlin · Jetpack Compose Material3 · AndroidX DataStore · no третьих зависимостей. minSdk 26, targetSdk 34.

## Разработка

```bash
git clone https://github.com/maxxleon84-png/samsung-tv-remote
cd samsung-tv-remote
./gradlew test         # unit-тесты протокола и ViewModel
./gradlew assembleDebug # debug APK
```

CI собирает debug APK на каждый push, релизный APK — на тег `v*.*.*`.

## Лицензия

MIT — см. [LICENSE](LICENSE).
