# GeoInt
Geo data interface plugin

Cordova plugin voor Android tbv Geo data

Op dit moment bevat het slechts test data.

Toevoegen van de plugin aan het Cordova project:
    cordova plugin add https://github.com/SanderStar/GeoInt.git

Verwijderen van de plugin aan het project:
    cordova plugin rm https://github.com/SanderStar/GeoInt.git

Aanroepen van de code (API):
```javascript
var success = function(message) {
    alert(message);
  }

  var failure = function() {
    alert("Error calling GeoInt Plugin");
  }

  cordova.plugins.GeoInt.coolMethod("World", success, failure);
```

Resultaat (message):
HelloWorld
