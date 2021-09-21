# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

This library is still a work in progress, and is currently built around RS2 Caches 647 to be specific. 
It currently only supports reading from the cache as of now.

# Features
- Cache Reading

# TODO
- Cache Writing
- Flat file system for unpacking the cache files into a raw format that can be git versioned.

# Usage
Index -> Group -> File

### Getting an index
```val index = store.index(5)```

### Getting a group by group id
```val group = store.group(5, 360)```

```val group = store.group(store.index(5), 360)```

### Getting a group by group name
```val group = store.group(5, "m${50}_${50}")```

```val group = store.group(store.index(5), "m${50}_${50}")```

### Getting a file from a group by id
```val file = store.file(2, 26, 1000)```

```val file = store.file(store.index(2), 26, 1000)```

### Looping multiple groups from an index
    store.index(21).use { index ->
        (0 until index.expand()).forEach {
            val data = store.file(index, it ushr 8, it and 0xFF).data
        }
    }

### Looping multiple files from a group
    store.index(2).use { index ->
        group.files(26).forEach {
            val data = store.file(index, it.groupId, it.id).data
        }
    }


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
