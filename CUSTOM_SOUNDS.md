# Custom Train Sounds

Train Whistles loads custom sounds through Minecraft resource packs. It lists any loaded sound event whose path begins with `train_sound/`.

## Resource Pack Layout

```text
My Train Sounds/
├─ pack.mcmeta
└─ assets/
   └─ my_train_sounds/
      ├─ sounds.json
      └─ sounds/
         └─ train_sound/
            └─ deep_horn.ogg
```

`pack.mcmeta` for Minecraft 1.21.1:

```json
{
  "pack": {
    "pack_format": 34,
    "description": "My Train Sounds"
  }
}
```

`assets/my_train_sounds/sounds.json`:

```json
{
  "train_sound/deep_horn": {
    "sounds": [
      {
        "name": "my_train_sounds:train_sound/deep_horn",
        "stream": false
      }
    ]
  }
}
```

This registers `my_train_sounds:train_sound/deep_horn` in the mod's sound-selection screen.

## Sound Requirements

- Use Ogg Vorbis, not Opus.
- Use lowercase file and folder names without spaces.
- Omit the `.ogg` extension from `sounds.json`.
- Prefer mono audio for positional sound.
- Make the recording loop seamlessly because it repeats while the rope remains active.

## Installation

1. Place the folder or zipped pack in the Minecraft instance's `resourcepacks` directory.
2. Enable it from the Resource Packs menu.
3. Press `F3+T` to reload resources, then reopen the sound-selection screen.
4. Empty-hand right-click a Train Sound Post and select the custom sound.

Every player who needs to hear the sound must have the resource pack enabled. Multiplayer servers can distribute the same zip through Minecraft's server resource-pack setting.
