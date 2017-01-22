var map = (function() {
    var _map = null;
    var _me = null;

    return {
        init: function(ymaps, mapId) {
            _map = new ymaps.Map(mapId, appSettings.mapOptions);
        },
        showMe: function(coords) {
            if (_me == null) {
                _me = new ymaps.Placemark(coords, {}, {preset: 'islands#redCircleDotIcon'});
                _map.geoObjects.add(_me);
                _map.setCenter(coords);
            } else {
                _me.geometry.setCoordinates(coords);
            }
        }
    }
}());

var game = (function() {
    var _myPos = null;
    var _onUpdate = null;

    return {
        init: function(ymaps, mapId, onInit) {
            map.init(ymaps, mapId);
            navigator.geolocation.watchPosition(function (position) {
                var _myPos = [position.coords.latitude, position.coords.longitude];
                console.log("my position: " + _myPos);
                map.showMe(_myPos);
            }, function () {}, appSettings.posWatchOptions);

            onInit();
        },
        connect: function(callbacks) {
            _onUpdate = callbacks.onUpdate;
            console.log("Game connecting....");
            callbacks.onConnect();
        },
        disconnect: function(onDisconnect) {
            console.log("Game disconnecting....");
            onDisconnect();
        }
    }
}());

var dashboard = (function() {
    var connect = function(pin) {
        showBlock("loading");
        game.connect({
            onConnect: function() {
                $pinSpan.text(pin);
                showBlock("info");
            },
            onUpdate: function() {
                console.log("Update callback");
            }
        });
    };

    var disconnect = function() {
        showBlock("loading");
        game.disconnect(function() {
            $pinSpan.text("");
            $pinEdit.val("");
            showBlock("input");
        });
    };

    var showBlock = function(block) {
        $pinInputBlock.toggle(block == "input");
        $loadingBlock.toggle(block == "loading");
        $infoBlock.toggle(block == "info");
    };

    var $dashboard = $("#dashboard");
    var $pinInputBlock = $("#pin-input-block");
    var $loadingBlock = $("#loading-block");
    var $infoBlock = $("#info-block");
    var $pinEdit = $("#pin-edit");
    var $startBtn = $("#start-btn");
    var $exitBtn = $("#exit-btn");
    var $pinSpan = $("#pin-span");

    $startBtn.on("click", function() {
        connect($pinEdit.val());
    });

    $pinEdit.keyup(function(e) {
        if (e.keyCode == 13) {
            $startBtn.click();
        }
    });

    $exitBtn.on("click", function() {
        disconnect();
    });

    return {
        show: function() {
            $dashboard.show();
        }
    }
}());

function initGame(ymaps) {
    game.init(ymaps, "map", function() {
        dashboard.show();
    });
}
