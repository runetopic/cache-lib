# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

This library is still a work in progress, and is currently built around RS2 Caches. It is however Modular is it's nature, and we will support all revisions in the future.
It currently only supports reading from the cache, have only tested this with StructTypes so far

# Features
- Modular based implementation for different cache formats.
- Currently only supports reading from the cache

## TODO
- Rework how files are being loaded into the archives (Super inefficient atm)
- Optimize the archive & index building
- Cache Writing
- Find archive by name
- Flat file system to unpacking and packing of the cache in the raw file formats

## Usage

'''    store.group(5).use {
        val data = store.getFileData(it.getFile("m${50}_${50}"))
        logger.debug { data.contentToString() }
    }

    val loader = StructTypeLoader()
    val structs = mutableListOf<StructType>()

    store.group(2).use { group ->
        val files = group.getFiles()[26]

        files.entries.forEach {
            structs.add(loader.decode(it.id, store.getFileData(it.id, files)))
        }
    }
'''


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
