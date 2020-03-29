# ReaktivePM

This port of [RxPm](https://github.com/dmdevgo/RxPM) library based on [Reaktive](https://github.com/badoo/Reaktive).

## Current project status:
- Ported RxPm to Reaktive
- Worked Android sample
- Not implemented rpm common classes for iOS - need help.
- Not all classes from common module available in xCode project after compilation, for example: PmView - need help.

## Installation
root build.gradle
```groovy
allprojects {
    repositories {
        maven { url = "https://dl.bintray.com/garage-dev/kmp" }
    }
}
```

project build.gradle
```groovy
dependencies {
    commonMainApi("dev.garage.kmp:rpm:0.0.1-alpha")
}
```

settings.gradle
```groovy
enableFeaturePreview("GRADLE_METADATA")
```