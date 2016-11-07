# firebaseAnalytics-source
Firebase Analytics plugin source code for corona sdk

Note still work in progress(beta)
Also you need corona enterprise until ready for marketplace

Quick start:

Please note this assumes you setup everything in your firebase console

##Andorid 

paste in google app id (Firebase calls it "App ID") inside strings.xml(res/values/strings.xml) under "google_app_id"

##iOS

insert GoogleService-Info.plist inside Corona folder


##Snippets

init
```
firebase.init()
```

LogEvent
```
firebase.LogEvent("select_content", {content_type = "hello", item_id= "world"})
```

SetUserProperties
```
firebase.SetUserProperties("favorite_food", "burger")
```
