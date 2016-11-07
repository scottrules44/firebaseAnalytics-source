local firebase = require "plugin.firebaseAnalytics"

firebase.init()
timer.performWithDelay( 10000, function (  )
    firebase.LogEvent("select_content", {content_type = "hello", item_id= "world"})
end )
