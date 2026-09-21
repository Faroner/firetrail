# FireTrail — Огненный след + Дай пять

Minecraft 1.20.1 / Forge 47.4.0 / Iron's Spells 'n Spellbooks 3.16.3.

## Дай пять

Заклинание `firetrail:high_five` создаёт отдельный GeckoLib-клон игрока. Клон плавно появляется и подходит на расстояние 1 блока, поворачивается к игроку, затем GeckoLib-анимация синхронно поднимает его правую руку; настоящий игрок получает зеркальное движение руки через клиентский render hook. В момент контакта появляются частицы и звук, затем клон плавно отходит и растворяется, после чего игрок получает +10 HP.

## GeckoLib

Проект использует GeckoLib **4.8.4** для Forge 1.20.1. GeckoLib должен быть доступен как мод в игровой сборке; Iron's Spells 'n Spellbooks также использует GeckoLib.

Ресурсы анимации находятся здесь:

- `assets/firetrail/geo/high_five_clone.geo.json`
- `assets/firetrail/animations/high_five_clone.animation.json`

Модель использует кости `root`, `body`, `head`, `right_arm`, `left_arm`, `right_leg`, `left_leg`.
