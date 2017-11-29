var exec = require('cordova/exec'),
    utils = require('cordova/utils'),
    argscheck = require('cordova/argscheck'),
    timers = {};

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, 'GeoInt', 'coolMethod', [arg0]);
};

exports.getLocation = function(success, error) {
    exec(success, error, 'GeoInt', 'getLocation');
};

exports.stopLocation = function(success, error) {
    exec(success, error, 'GeoInt', 'stopLocation');
};

exports.getCurrentSensor = function(success, error) {
    exec(success, error, 'GeoInt', 'getCurrentSensor');
};

exports.watchCurrentSensor = function(success, error, options) {
  // Default interval (100 msec)
  var frequency = (options !== undefined && options.frequency !== undefined) ? options.frequency : 100;

  var id = utils.createUUID();
  timers[id] = window.setInterval(function () {
    fusion.getCurrentSensorFusion(successCallback, errorCallback);
  }, frequency);

  return id;
};

exports.clearWatch = function(id) {
  // Stop javascript timer & remove from timer list
  if (id && timers[id]) {
    clearInterval(timers[id]);
    delete timers[id];
  }
};

exports.startSensor = function(success, error) {
    exec(success, error, 'GeoInt', 'startSensor');
};

exports.stopSensor = function(success, error) {
    exec(success, error, 'GeoInt', 'stopSensor');
};
