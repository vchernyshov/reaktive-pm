# ReaktivePM
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [ ![Download](https://api.bintray.com/packages/garage-dev/kmp/reaktive-pm/images/download.svg) ](https://bintray.com/garage-dev/kmp/reaktive-pm/_latestVersion) ![kotlin-version](https://img.shields.io/badge/kotlin-1.3.71-orange)

This port of [RxPM](https://github.com/dmdevgo/RxPM) library based on [Reaktive](https://github.com/badoo/Reaktive).

## Current project status:
- Ported RxPM to Reaktive
- Worked Android sample
- Worked iOS sample
- Implemented control to deal with permissions

## What can be improved:
- Implementation of RetainMode at ```PmUiViewControllerDelegate```
- ```UiViewControllerNavigationMessageDispatcher.getParent``` need some consultation about controllers hierarchy and approaches to manage navigation at iOS

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
    // main library
    commonMainApi("dev.garage.kmp:rpm:0.2.0-beta")
    // permissions module
    commonMainApi("dev.garage.kmp:rpm-permissions:0.2.0-beta")
}
```

settings.gradle
```groovy
enableFeaturePreview("GRADLE_METADATA")
```

Podfile for iOS app, not sure but should work
```
pod 'MultiPlatformLibraryRpm', :git => 'https://github.com/vchernyshov/reaktive-pm.git', :tag => 'release/0.2.0-beta'
```

## Next steps at this project and in KotlinMultiplatform in general:
- Port [RxBinding](https://github.com/JakeWharton/RxBinding) to Reaktive
- Port [AdapterDelegate](https://github.com/nullgr/app-core/tree/master/core-adapter) to KMP, already have version without rx, need to create iOS implementation.
- Add controls for location, pagination, working with map and ect.
- Create interfaces for existing control bindings to simplify implementation in custom views
- KMP clustering based on [GoogleMapUtils](https://github.com/googlemaps/android-maps-utils), already have Kotlin port, without dependency on map provider. Currently in private repo.
