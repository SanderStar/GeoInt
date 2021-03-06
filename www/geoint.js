var exec = require('cordova/exec'),
    utils = require('cordova/utils'),
    argscheck = require('cordova/argscheck'),
    timers = {},

sensor = {

  coolMethod: function(args, success, error) {
     exec(success, error, "GeoInt", "coolMethod", [args]);
  },

  getTrunk: function(success, error) {
    exec(success, error, "GeoInt", "getTrunk");
  },

  getTrunkTest: function(success, error) {
    exec(success, error, "GeoInt", "getTrunkTest");
  },

  startLocation: function(success, error) {
     exec(success, error, "GeoInt", "startLocation");
  },

  stopLocation: function(success, error) {
     exec(success, error, "GeoInt", "stopLocation");
  },

  currentLocation: function(success, error) {
    exec(success, error, "GeoInt", "currentLocation");
  },

  getCurrentSensor: function(success, error) {
     exec(success, error, "GeoInt", "getCurrentSensor");
  },

  watchCurrentSensor: function(success, error, options) {
    // Default interval (100 msec)
    var frequency = (options !== undefined && options.frequency !== undefined) ? options.frequency : 100;

    var id = utils.createUUID();
    timers[id] = window.setInterval(function () {
      sensor.getCurrentSensor(success, error);
    }, frequency);

    return id;
  },

  clearWatch: function(id) {
    // Stop javascript timer & remove from timer list
    if (id && timers[id]) {
      clearInterval(timers[id]);
      delete timers[id];
    }
  },

  startSensor: function(success, error) {
      exec(success, error, "GeoInt", "startSensor");
  },

  stopSensor: function(success, error) {
      exec(success, error, "GeoInt", "stopSensor");
  }

};

module.exports = sensor;


