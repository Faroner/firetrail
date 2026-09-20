# Fire Trail — custom Iron's Spells 'n Spellbooks spell

Minecraft 1.20.1 / Forge 47.4.0 / Iron's Spells 'n Spellbooks 1.20.1-3.16.3 / Java 17

## What it does

The custom spell `Огненный след`:
- appears in front of the caster;
- moves forward for 4 seconds;
- emits Flame and Smoke particles;
- has no collision;
- does not damage entities;
- does not set blocks or entities on fire.

The spell registry id is:

    firetrail:fire_trail

For testing, Iron's Spells 'n Spellbooks exposes a cast command. Try:

    /iss cast firetrail:fire_trail

If the exact command syntax differs in your installed build, run `/iss` and use its `cast` command help.

## Build

Use JDK 17.

With Gradle installed:

    gradle build

Output:

    build/libs/firetrail-1.0.0.jar

## Runtime dependencies

Your Minecraft instance must already contain Iron's Spells 'n Spellbooks 1.20.1-3.16.3 and its required dependencies.

Known matching dependencies for this 1.20.1 release:
- Iron's Lib 1.20.1-2.1.0
- GeckoLib 1.20.1-4.8.4
- Curios API 5.14.1+1.20.1
- playerAnimator 1.0.2-rc1+1.20

## Note

The icon in this project is original and generated for this addon. No Iron's Spells 'n Spellbooks assets are included.
