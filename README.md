# Forestry — порт на Minecraft 26.1.2

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-green)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-64.0.4-orange)](https://files.minecraftforge.net/)
[![License](https://img.shields.io/badge/License-LGPL--3.0-blue.svg)](LICENSE.txt)
[![Status](https://img.shields.io/badge/Status-WIP-yellow)](PORT_STATUS.md)

**Forestry Community Edition**, перенесённый с **1.20.1** на **Minecraft 26.1.2** (Forge).

> ⚠️ **Ранний порт, не для игры.** Мод компилируется и загружается в клиент, но большая часть функционала ещё не работает. Сейчас в основном видны зарегистрированные блоки; машины, пчёлы, фермы, GUI и многие текстуры — в процессе миграции.

## Требования

| Компонент | Версия |
|-----------|--------|
| Minecraft | **26.1.2** |
| Forge | **26.1.2-64.0.4** |
| Java | **25** (JDK 25, toolchain в `build.gradle`) |

## Сборка из исходников

```powershell
# Windows — укажите JDK 25, если не в PATH
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot"

.\gradlew.bat build
```

Готовый JAR: `build/libs/forestry-26.1.2-2.10.0-26.1-port.jar`

Запуск тестового клиента:

```powershell
.\gradlew.bat runClient
```

## Установка (если есть собранный JAR)

1. Установите **Minecraft 26.1.2** и **Forge 64.0.4**.
2. Скопируйте JAR в папку `mods`.
3. Запустите игру. Ожидайте неполный контент — см. [PORT_STATUS.md](PORT_STATUS.md).

## Что уже работает (частично)

- Компиляция и загрузка мода без краша при старте мира
- Регистрация блоков, предметов и креативных вкладок
- Базовая миграция API: `Identifier`, EventBus 7, рецепты, capabilities
- Минимальные экраны машин (заглушки GUI)
- Часть клиентского рендера (BER, модели пчёл, саженцев)

## Что ещё не работает

- **Геймплей**: пчёлы, фермы, генетика, почта, рюкзаки, лодки и др.
- **GUI**: полноценные виджеты, ledgers, танки
- **Текстуры**: часть предметов и машин — фиолетово-чёрные или без иконки
- **Совместимость**: JEI, Patchouli, KubeJS, Curios
- **Деревенские жители**: торговля пчеловодом / лесоводом

Подробный список фаз и исключений из сборки — в [PORT_STATUS.md](PORT_STATUS.md).

## Происхождение кода

Исходный код основан на [Forestry Community Edition](https://github.com/thedarkcolour/ForestryCE) (LGPL-3.0), версия **2.10.0** для Minecraft **1.20.1**.

Этот репозиторий — **неофициальный порт** для экспериментов и совместной доработки. Не связан с оригинальными авторами Forestry CE.

## Участие

Баги и пожелания — [Issues](https://github.com/nexxus-pixel/forestry-26.1-port/issues).  
Pull request'ы приветствуются, особенно по:

- клиентскому рендеру (GuiGraphicsExtractor, BER, ItemModel)
- восстановлению исключённых модулей (`build.gradle` → `sourceSets.main.java { exclude ... }`)
- текстурам и `assets/forestry/items/*.json` для MC 26.1

## Лицензия

Код Forestry распространяется под **GNU LGPL v3** — см. [LICENSE.txt](LICENSE.txt) и [ForestryCE](https://github.com/thedarkcolour/ForestryCE).

---

## English summary

Unofficial **Forestry CE** port from 1.20.1 to **Minecraft 26.1.2** (Forge 64.0.4). Early WIP: mod loads and blocks appear, but gameplay, GUIs, and many textures are broken or missing. Build with JDK 25 and `gradlew build`. See [PORT_STATUS.md](PORT_STATUS.md) for migration details.
