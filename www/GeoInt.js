var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'GeoInt', 'coolMethod', [arg0]);
};

exports.getLocation = function (arg0, success, error) {
    exec(success, error, 'GeoInt', 'getLocation', [arg0]);
};
