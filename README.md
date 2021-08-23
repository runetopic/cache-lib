# Xlite 2.0 Cache Library

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![License](https://img.shields.io/github/license/xlite2/xlite)](#)

Work in progress.

# Features
- Service based implementation for different cache formats.
- Externalized definitions for easy loading and mapping cache data to useable objects.

## Usage
```
val serviceRS2 by inject<ICacheService>()
val loader = CacheLoader(serviceRS2)
val indexData = loader.readIndex(5)
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to write unit test and or update any test that might have been impacted.
