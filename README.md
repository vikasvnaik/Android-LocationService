# architecture
MVVM, Kotlin, Koin(Dependency Injection)

#App
* Location service 
    - Capture location in background with sticky service(START_STICKY) 
    - Capturing location every 10sec
    - Captured location data is pushed to Firebase
    - Also pushed to dashboard using live data to show vehicle movement
    - Work manager to check if service is running and restart if stopped. Work manager runs every 15min.
    
* Firebase 
    - Have setup firebase 
    - Enabled real time database, crashlytics
    - Have enabled database persistence to store data in offline and push as network is available
    
* App flow
    - On launch Splash screen opens
    - Asks for location and background location permissions
    - Check for if GPS is enabled
    - If all are set redirects to Dashboard screen
    - On Duty/Off Duty button
        - By default Off Duty 
        - On switch to On Duty location tracking is initiated
        - On switch to off Duty location tracking will be stopped
    - Subscription to location live data 
        - As location changes from background service data is pushed to dashboard using live data
        - As location updated truck marker on the map is updated
        - And also fetching weather report for the current location which 
        - Using free weather API so not updating frequently and has limited calls so might fail while testing if the limit is crossed. 

