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

# Gradle
Just use cache if you do not require any of the revision specific loaders.
```
cache = { module = "com.runetopic.cache:cache", version.ref "1.4.3-SNAPSHOT" }
loader = { module = "com.runetopic.cache:loader", version.ref "647.3.0-SNAPSHOT" }
```

# Usage
Index -> Group -> File

### Getting an index
```val index = store.index(indexId = 5)```

### Getting a group by group id
```val group = store.group(indexId = 5, groupId = 360)```

```val group = store.group(index = store.index(5), groupId = 360)```

### Getting a group by group name
```val group = store.group(indexId = 5, groupName = "m${50}_${50}")```

```val group = store.group(index = store.index(5), groupName = "m${50}_${50}")```

### Getting a file from a group by id
```val file = store.file(indexId = 2, groupId = 26, fileId = 1000)```

```val file = store.file(index = store.index(2), groupId = 26, fileId = 1000)```

### Looping multiple groups from an index
    store.index(indexId = 21).use { index ->
        (0..index.expand()).forEach {
            val data = store.file(index = index, groupId = it ushr 8, fileId = it and 0xFF).data
        }
    }

### Looping multiple files from a group
    store.index(indexId = 2).use { index ->
        index.files(groupId = 26).forEach {
            val data = store.file(index = index, groupId = it.groupId, fileId = it.id).data
        }
    }

### Getting the reference table of an index and group by id.
```store.groupReferenceTable(indexId = 255, groupId = 255)```

### Getting an index reference table size by id
```store.indexReferenceTableSize(indexId = 28)```

### Getting a group reference table size by name
```store.groupReferenceTableSize(indexId = 30, groupName = "windows/x86/jaclib.dll")```

### Getting a group reference table size by id
```store.groupReferenceTableSize(indexId = 30, groupId = 6)```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
