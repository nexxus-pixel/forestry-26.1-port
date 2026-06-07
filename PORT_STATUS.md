# Статус порта Forestry → 26.1.2

Версия мода: `2.10.0-26.1-port`  
База: Forestry CE **2.10.0** (1.20.1)  
Цель: Minecraft **26.1.2**, Forge **64.0.4**

## Фазы миграции

| Фаза | Описание | Статус |
|------|----------|--------|
| 1 | `ResourceLocation` → `Identifier` | ✅ Готово |
| 2 | EventBus 7, lifecycle, capabilities | ✅ Готово |
| 3 | Система рецептов (MapCodec / StreamCodec) | ✅ Готово |
| 4 | Клиентский рендер (BER, модели, tint) | 🔄 В процессе |
| 5 | Item/Block API, регистрация, runtime | 🔄 В процессе |
| 6 | Торговля жителей | ⏳ Ожидает |
| 7 | JEI, Patchouli, KubeJS, Curios | ⏳ Ожидает |

## Текущее состояние в игре

**Работает (частично):**

- Запуск клиента, загрузка мира
- Регистрация блоков и предметов
- Креативные вкладки (с дедупликацией для 26.1)
- Минимальные экраны машин (`ForestryMenuClientSetup`)
- Часть BER и item-моделей (пчёлы, саженцы, машины)

**Не работает / отключено в сборке:**

- Полноценные GUI (виджеты, ledgers, танки)
- Почта (`forestry/mail/**`)
- Рюкзаки (`forestry/storage/**`)
- Лодки и часть arboriculture entities
- Многие предметы Phase 5 (инструменты, контейнеры с жидкостью)
- Villager trades
- JEI / compat модули

## Исключения из компиляции

В `build.gradle` намеренно исключены пакеты, которые ещё не портированы на API 26.1. Основные группы:

```
forestry/compat/**          — совместимость с другими модами
forestry/**/client/**       — старые client handlers (кроме minimal setup)
forestry/core/gui/**        — полный GUI (кроме minimal/)
forestry/storage/**         — рюкзаки
forestry/mail/**            — почта
forestry/arboriculture/entities/** — лодки
forestry/apiculture/particles/**   — частицы
... и др. (см. build.gradle)
```

Чтобы вернуть модуль — уберите соответствующий `exclude`, исправьте ошибки компиляции и протестируйте в `runClient`.

## Известные проблемы с текстурами

1. **MC 26.1** требует `assets/forestry/items/<id>.json` для каждого предмета — без них фиолетово-чёрная иконка.
2. Нельзя смешивать атласы `items.png` и `blocks.png` в одной item-модели.
3. BER-машины используют block-atlas; пути спрайтов нормализуются через `BerRenderHelper.atlasTexture()`.
4. Загрузчики `filled_crate`, `fluid_container`, `sapling_ge`, `butterfly_ge` — ещё не полностью портированы.

## Сборка

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot"
.\gradlew.bat compileJava   # проверка компиляции
.\gradlew.bat runClient     # тест в dev-среде
.\gradlew.bat build         # release JAR
```

После изменений в Java-коде для runClient иногда нужен `.\gradlew.bat compileJava --rerun-tasks`, иначе Gradle может использовать устаревшие классы.

## Приоритеты для контрибьюторов

1. Порт `filled_crate` / `fluid_container` item loaders
2. Миграция GUI на GuiGraphicsExtractor
3. Восстановление `CoreClientHandler` и модульных client handlers
4. Tint sources для всех `IColoredItem`
5. Почта и рюкзаки (крупные подсистемы)
