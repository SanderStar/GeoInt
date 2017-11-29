var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, 'GeoInt', 'coolMethod', [arg0]);
};

exports.getLocation = function(success, error) {
    exec(success, error, 'GeoInt', 'getLocation');
};

exports.stopLocation = function(success, error) {
    exec(success, error, 'GeoInt', 'stopLocation');
};

exports.startSensor = function(success, error) {
    exec(success, error, 'GeoInt', 'startSensor');
};

exports.stopSensor = function(success, error) {
    exec(success, error, 'GeoInt', 'stopSensor');
};
