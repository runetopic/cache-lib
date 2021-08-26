# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

Work in progress.

# Features
- Service based implementation for different cache formats.

## Usage
```
val service = CacheServiceRS2(CacheConfiguration.properties.getProperty("cache.location"))
val loader = CacheLoader(service)

val index = loader.readIndex(2)
val archive = index.archives[26]

val files = archive.files

val structTypeLoader = StructTypeLoader()

val structTypes = mutableListOf<StructType>()

files.indices.forEach {
    val entry = files[it]
    val data = entry.decode(it, service.readArchive(archive), archive)
    structTypes.add(structTypeLoader.decode(entry.id, data))
}
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
