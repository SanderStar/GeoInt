# GeoInt
Geo data interface plugin

Cordova plugin voor Android tbv Bluetooth, Geo en Sensor data.
- Bluetooth connectie Esders koffer
- GPS locatie
- Accelerometer data
- Orientation data


Toevoegen van de plugin aan het Cordova project:
```
cordova plugin add https://github.com/SanderStar/GeoInt.git
```

Verwijderen van de plugin uit het project:
```
cordova plugin rm https://github.com/SanderStar/GeoInt.git
```

### Aanroepen van de code (API):


##### Dummy aanroep Helloworld
```javascript
var success = function(message) {
    alert(message);
  }

  var failure = function() {
    alert("Error calling GeoInt Plugin");
  }

  navigation.sensor.coolMethod("World", success, failure);
```

Resultaat (message):
```
HelloWorld
```

##### Esders koffer

Aanroepen van de koffer via de Esders IR app.

```javascript
var success = function(result) {
    alert(result);
}

var error = function(error) {
    console.log(error);
}
navigation.sensor.getTrunk(success, error);
```

Resultaat (result):
Is een JSON string.

Aanroepen van de Cordova plugin tbv test Cordova plugin.

```javascript
var success = function(result) {
    alert(result);
}

var error = function(error) {
    console.log(error);
}
navigation.sensor.getTrunkTest(success, error);
```

Resultaat (result):
Is een vaste JSON string.


##### Sensor data
```javascript
var watchSensorId;

// Will start the sensor and deliver sensor data at a specified interval
function startSensor() {
      // Timer interval 1000 milliseconds
      var options = { frequency: 1000 };

      // Fired at defined interval (see options)
      var success = function(message) {
        // Data is returned in JSON
        alert(message);
      }

      var failure = function(error) {
        alert("Error calling plugin " + error);
      }

      watchSensorId = navigation.sensor.watchCurrentSensor(success, failure, options);
}

function stopSensor() {
    navigation.sensor.clearWatch(watchSensorId);
}
```

##### Locatie data
Later
