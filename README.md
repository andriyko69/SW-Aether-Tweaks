# [SW] Aether Tweaks

Small addon for The Aether that simplifies the accessories system and adjusts gameplay flow.

## Features

### Sun Altar restrictions

* Only OP players can change time using the Sun Altar
* Non-OP players receive an action bar message
* Aether time progression is disabled

### Accessories overhaul

* Only **gloves** and **capes** remain as accessory slots
* All other accessory categories are removed (not hidden)
* `Shield of Repulsion` is treated as a cape

### Inventory changes

#### Survival / non-creative players

* Opening inventory redirects to the accessories screen
* Vanilla inventory is not accessible through normal input
* Crafting grid is still available
* Custom XP label is displayed:

  ```
  XP: <player level>
  ```

#### Creative players

* Creative inventory remains unchanged
* Two additional accessory slots are available (cape + gloves)

## Design goals

* Reduce UI clutter
* Keep accessories focused and meaningful
* Integrate accessories into normal gameplay flow

## Compatibility

* Minecraft: 1.21.1
* NeoForge: 21.1.x
* The Aether: 1.5.10 (NeoForge)
