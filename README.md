# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

This library is still a work in progress, and is currently built around RS2 Caches 647 to be specific. 
It currently only supports reading from the cache as of now.

# Features
- Cache Reading
- 

## TODO
- Rework how files are being loaded into the archives (Super inefficient atm)
- Optimize the archive & index building
- Cache Writing
- Find archive by name
- Flat file system to unpacking and packing of the cache in the raw file formats

## Usage

# Getting file by name
```store.file(store.group(5), "m${50}_${50}")```

# Getting file entry by id
```
store.group(2).use { group ->
   val js5File = group.files[26]

    js5File.entries.forEach { fileEntry ->
        val entry = store.entry(group, 26, fileEntry.id)
        add(read(ByteBuffer.wrap(entry.data), StructEntryType(fileEntry.id)))
    }
}
```


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
