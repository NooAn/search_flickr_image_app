# search_flickr_image_app

Application for searching images by keywords from Flick service.

- Kotlin
- MVP
- Network layer in a separate gradle module 
- ImageLoader with Android's LruCache
- Homemade DI
- Unit and UI test
- The data is retrieved from flickr API

Built apk: https://github.com/NooAn/search_flickr_image_app/blob/master/apk/debug/app-debug.apk

The second commit in master branch based on ViewModel and coroutines.
The last commit just Mvp story.

Known issues and possible improvements:
- *Add empty state for ui
- *Add retry mechanish
- *Add file cache 
- *Can use Flow with debounce when user type in search field
- *Photo list can be more tham 2mb
- *No implemented progress bar but view methods contains it
- *Add checking internet connectivity
- *Add animations when image appear
- *Add download cancellation
- *Fix some bugs with adapter
- *Make one more module for imageLoader (or use Glide)
- *Add more tests
