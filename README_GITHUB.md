# Fire Trail — GitHub Actions build

Этот вариант предназначен для сборки мода полностью онлайн.

## Требуется

- GitHub аккаунт
- браузер
- Minecraft 1.20.1 Forge
- Iron's Spells 'n Spellbooks 3.16.3 в игре

На ПК не требуется устанавливать Java, Gradle, IntelliJ IDEA или Forge MDK.

## Как загрузить

1. Создай новый пустой репозиторий на GitHub.
2. Открой репозиторий.
3. Нажми `Add file` → `Upload files`.
4. Загрузи содержимое этого ZIP в репозиторий.
5. Сделай `Commit changes`.

GitHub Actions автоматически запустит сборку.

## Где получить JAR

После завершения сборки:

`Actions` → `Build Fire Trail` → последний запуск → `Artifacts` → `firetrail-1.0.0`

Скачанный ZIP содержит готовый:

`firetrail-1.0.0.jar`

Его можно положить в папку `mods` Minecraft.

## Если workflow не запускается автоматически

Открой:

`Actions` → `Build Fire Trail` → `Run workflow`

и запусти его вручную.

## Важно

Этот проект создаёт декоративное заклинание `firetrail:fire_trail`.

Оно:
- появляется перед игроком;
- летит вперёд;
- оставляет Flame и Smoke частицы;
- исчезает через 4 секунды;
- не наносит урон;
- не поджигает мобов или блоки.

Если сборка выдаст ошибку API Iron's Spells 'n Spellbooks 3.16.3, пришли сюда текст ошибки из Actions — по нему можно исправить проект.
