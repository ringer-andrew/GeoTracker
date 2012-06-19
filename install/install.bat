adb uninstall com.professionalperformance.geotracker

adb install GeoTracker.apk

adb shell am start -n com.professionalperformance.geotracker/.EnforceGPSActivity

adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
