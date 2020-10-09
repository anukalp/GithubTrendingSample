
# Trending list of github repo's, written and implemented in kotlin. Dagger, MVVM, LiveData, Retrofit, Data Binding, Coroutines, Flows, Espresso Ui and unit test cases.

[![Language](https://img.shields.io/badge/Language-Kotlin-orange)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](https://developer.android.com/docs)

## Functional Requirements
1. The app should fetch the trending repositories from the provided public API and display it to the
users.
2. While the data is being fetched, the app should show a loading state. Shimmer animation is
optional.
3. If the app is not able to fetch the data, then it should show an error state to the user with an
option to retry again.
4. All the items in the list should be in their collapsed state by default and can be expanded on
being tapped.
5. Tapping any item will expand it to show more details and collapse any other expanded item.
Tapping the same item in expanded state should collapse it.
6. The app should give a pull-to-refresh option to the user to force fetch data from remote.

## Non Functional Requirements
1. The app should preferably support minimum Android API level 19.
2. The app should be able to handle configuration changes (like rotation)
3. The app should have 100% offline support. Once the data is fetched successfully from remote, it
should be stored locally and served from cache until the cache expires.
4. The cached data should only be valid for a duration of 2 hour. After that the app should attempt
to refresh the data from remote and purge the cache only after successful data fetch.
5. Uses MVVM + LiveData + Data Binding + Coroutines + Flows
6. Dagger 2 for dependency injection framework
7. App has espresso ui (with custom matchers and mockwebserver) + unit test cases
8. Retrofit + okhttp for networking and offline support
9. Follows the solid design principals
10. EspressoTestSuite is created that runs the ui test cases to validate 
11. Added 300 msec delays to show the espresso tests getting run

## Documentation
Anukalp Katyal(katyalanukalp@gmail.com)
