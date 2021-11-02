# RuneTopic Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

A cache library written in Kotlin. 

# Requirements
- Kotlin Version 1.5
- Java Version 16

# Supported
- RS2 (414-772)
- RS3 (773-~788)  
- OSRS (1-current)

# Features
- Cache Reading
- Definitions/Providers Loading
- Very Fast (Limited by I/O)

# TODO
- Cache Writing
- Flat File System
- Ondemand Data Caching
- 317 and older support
- Tests

# Implementation
Just use cache if you do not require any of the revision specific loaders.
```
cache = { module = "com.runetopic.cache:cache", version.ref "1.4.19-SNAPSHOT" }
loader = { module = "com.runetopic.cache:loader", version.ref "647.6.4-SNAPSHOT" }
```

```
//SNAPSHOTS
maven {
  url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
```

# Usage
Index -> Group -> File

### Creating a new JS5 store
```
val store = Js5Store(path = Path.of("/path/"), parallel = true)
```

### Getting an index
```
val index = store.index(indexId = 5)
```

### Getting a group by group id
```
val index = store.index(indexId = 5)
val group = index.group(groupId = 360)
```

### Getting a group by group name
```
val index = store.index(indexId = 5)
val group = index.group(groupName = "m50_50")
```

### Getting a file from a group by id
```
val index = store.index(indexId = 2)
val group = index.group(groupId = 26)
val file = group.file(fileId = 1000)
```

### Looping multiple groups from an index
    store.index(indexId = 19).use { index ->
        (0 until index.expand()).forEach {
            val data = index.group(it ushr 8).file(it and 0xFF).data
        }
    }

### Looping multiple files from a group
    store.index(indexId = 2).group(groupId = 26).files().forEach {
        val id = it.id
        val data = it.data
    }

### Getting the reference table of an index and group by id.
```store.groupReferenceTable(indexId = 255, groupId = 255)```

### Getting an index reference table size by id
```store.indexReferenceTableSize(indexId = 28)```

### Getting a group reference table size by name
```store.groupReferenceTableSize(indexId = 30, groupName = "windows/x86/jaclib.dll")```

### Getting a group reference table size by id
```store.groupReferenceTableSize(indexId = 30, groupId = 6)```

### Getting 255, 255 checksums with RSA/Whirlpool
```val checksums = store.checksumsWithRSA(exponent = BigInteger(""), modulus = BigInteger(""))```

### Getting 255, 255 checksums without RSA/Whirlpool
```val checksums = store.checksumsWithoutRSA()```

### An example of a single thread loading providers
```
objs().load(store)
npcs().load(store)
locs().load(store)
particles().load(store)
```

### An example of multiple threads parallel loading providers
```
val pool = Executors.newFixedThreadPool(4)
val providers = listOf(
    objs(),
    npcs(),
    locs(),
    particles()
)
val latch = CountDownLatch(providers.size)
providers.forEach {
    pool.execute {
        it.load(store)
        latch.countDown()
    }
}
latch.await()
pool.shutdown()
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
