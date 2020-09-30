# store-ping-client

[![Language](https://img.shields.io/badge/Language-Kotlin-orange)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](https://developer.android.com/docs)

Trending list of github repo's, written and implemented in kotlin.

#Functional Requirements
● The app should fetch the trending repositories from the provided public API and display it to the
users.
● While the data is being fetched, the app should show a loading state. Shimmer animation is
optional.
● If the app is not able to fetch the data, then it should show an error state to the user with an
option to retry again.
● All the items in the list should be in their collapsed state by default and can be expanded on
being tapped.
● Tapping any item will expand it to show more details and collapse any other expanded item.
Tapping the same item in expanded state should collapse it.
● The app should give a pull-to-refresh option to the user to force fetch data from remote.

#Non Functional Requirements
● The app should preferably support minimum Android API level 19.
● The app should be able to handle configuration changes (like rotation)
● The app should have 100% offline support. Once the data is fetched successfully from remote, it
should be stored locally and served from cache until the cache expires.
● The cached data should only be valid for a duration of 2 hour. After that the app should attempt
to refresh the data from remote and purge the cache only after successful data fetch.
● Uses MVVM + LiveData + Data Binding + Coroutines + Flows
● Dagger 2 for dependency injection framework
● App has espresso ui (with custom matchers and mockwebserver) + unit test cases
● Retrofit + okhttp for networking and offline support
● Follows the solid design principals
● EspressoTestSuite is created that runs the ui test cases to validate 
● Added 300 msec delays to show the espresso tests getting run

# Documentation
Anukalp Katyal(katyalanukalp@gmail.com)
