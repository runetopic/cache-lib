# RuneTopic Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)

A cache library written in Kotlin. 

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
```groovy
cache = { module = "com.runetopic.cache:cache", version.ref "2.0.0-SNAPSHOT" }
```

```groovy
//SNAPSHOTS
maven {
    url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
```

# Usage
Index -> Group -> File

### Creating a new JS5 store
```kotlin
val store = Js5Store(path = Path.of("/path/"), parallel = true)
```

### Getting an index
```kotlin
val index = store.index(indexId = 5)
```

### Getting a group by group id
```kotlin
val index = store.index(indexId = 5)
val group = index.group(groupId = 360)
```

### Getting a group by group name
```kotlin
val index = store.index(indexId = 5)
val group = index.group(groupName = "m50_50")
```

### Getting a file from a group by id
```kotlin
val index = store.index(indexId = 2)
val group = index.group(groupId = 26)
val file = group.file(fileId = 1000)
```

### Looping multiple groups from an index
```kotlin
store.index(indexId = 19).use { index ->
    (0 until index.expand()).forEach {
        val data = index.group(it ushr 8).file(it and 0xFF).data
    }
}
```

### Looping multiple files from a group
```kotlin
store.index(indexId = 2).group(groupId = 26).files().forEach {
    val id = it.id
    val data = it.data
}
```
### Getting the reference table of an index and group by id.
```kotlin
val size = store.groupReferenceTable(indexId = 255, groupId = 255)
```

### Getting an index reference table size by id
```kotlin
val size = store.indexReferenceTableSize(indexId = 28)
```

### Getting a group reference table size by name
```kotlin
val size = store.groupReferenceTableSize(indexId = 30, groupName = "windows/x86/jaclib.dll")
```

### Getting a group reference table size by id
```kotlin
val size = store.groupReferenceTableSize(indexId = 30, groupId = 6)
```

### Getting 255, 255 checksums with RSA/Whirlpool
```kotlin
val checksums = store.checksumsWithRSA(exponent = BigInteger(""), modulus = BigInteger(""))
```

### Getting 255, 255 checksums without RSA/Whirlpool
```kotlin
val checksums = store.checksumsWithoutRSA()
```

### An example of a single thread loading providers

```kotlin
val objProvider = objs().load(store)
val npcProvider = npcs().load(store)
val locProvider = locs().load(store)
val particleProvider = particles().load(store)
```

### An example of multiple threads parallel loading providers
```kotlin
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
