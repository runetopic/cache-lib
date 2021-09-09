# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

This library is still a work in progress, and is currently built around RS2 Caches 647 to be specific. 
It currently only supports reading from the cache as of now.

# Features
- Cache Reading

# TODO
- Cache Writing
- Flat file system to unpacking and packing of the cache in the raw file formats

# Usage

### Getting a specific group
```val group = store.group(5)```

### Getting a specific file by file id
```val file = store.file(5, 360)```

```val file = store.file(store.group(5), 360)```

### Getting a specific file by file name
```val file = store.file(5, "m${50}_${50}")```

```val file = store.file(store.group(5), "m${50}_${50}")```

### Getting a specific entry of a file by entry id
```val entry = store.entry(2, 26, 1000)```

```val entry = store.entry(store.group(2), 26, 1000)```

### Looping files from a specific group
```
            store.group(21).use { group ->
                (0 until group.expandedCapacity()).forEach {
                    val data = store.entry(group, it ushr 8, it and 0xFF).data
                }
            }
```

### Looping entries from a specific file
```
            store.group(2).use { group ->
                group.entries(26).forEach {
                    val data = store.entry(group, it.fileId, it.entryId).data
                }
            }
```


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
