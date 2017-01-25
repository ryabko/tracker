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
    var initialized = false;

    return {
        init: function(ymaps, mapId, onInit) {
            map.init(ymaps, mapId);
            navigator.geolocation.watchPosition(function (position) {
                _myPos = [position.coords.latitude, position.coords.longitude];
                console.log("my position: " + _myPos);
                map.showMe(_myPos);
                if (!initialized) {
                    onInit();
                    initialized = true;
                }
            }, function () {}, appSettings.posWatchOptions);
        },
        connect: function(pin, callbacks) {
            _onUpdate = callbacks.onUpdate;
            console.log("Game connecting.....");
            api.request("/users", "post", {
                pin: pin,
                lat: _myPos != null ? _myPos[0] : null,
                long: _myPos != null ? _myPos[1] : null
            }).done(function(data) {
                console.log("Game connected");
                console.log(data);
                Cookies.set("uid", data.id, {expires: 7});
                callbacks.onConnect();
            }).fail(function() {
                console.log("Game connection error");
            });
        },
        disconnect: function(onDisconnect) {
            console.log("Game disconnecting....");
            api.request("/users", "delete", {
                id: Cookies.get("uid")
            }).done(function() {
                Cookies.remove("uid");
                onDisconnect();
            }).fail(function() {
                console.log("Disconnecting error");
            })
        }
    }
}());

var api = (function() {
    return {
        post: function(path, data) {
            return req(path, "post", data);
        },
        put: function(path, data) {

        },
        request: function(path, method, data) {
            return $.ajax({
                url: appSettings.apiEndpoint + path,
                type: method,
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(data)
            });
        }
}
}());

var dashboard = (function() {
    var connect = function(pin) {
        showBlock("loading");
        game.connect(pin, {
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