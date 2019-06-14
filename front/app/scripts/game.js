var map = (function() {
    var _map = null;
    var _me = null;
    var _otherPlayers = {};
    var _checkPoints = {};
    var _destination = null;

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
        },
        showState: function(state, ownId) {
            var ids = [];
            for (var i = 0; i < state.players.length; i++) {
                var id = state.players[i].user.id;
                var preset = state.players[i].target ? 'islands#darkGreenCircleDotIcon' : 'islands#redCircleDotIcon';
                if (id == ownId) {
                    _me.options.set('preset', preset);
                    continue;
                }
                var coords = [state.players[i].user.latitude, state.players[i].user.longitude];
                var mark = _otherPlayers[id];
                if (mark) {
                    mark.geometry.setCoordinates(coords);
                    mark.options.set('preset', preset)
                } else {
                    mark = new ymaps.Placemark(coords, {}, {preset: preset});
                    _map.geoObjects.add(mark);
                    _otherPlayers[id] = mark;
                }
                ids.push(id);
            }
            for (var key in _otherPlayers) {
                if (_otherPlayers.hasOwnProperty(key)) {
                    if (ids.indexOf(key) == -1) {
                        _map.geoObjects.remove(_otherPlayers[key]);
                        delete _otherPlayers[key];
                    }
                }
            }

            var checkPointIds = [];
            for (i = 0; i < state.checkPoints.length; i++) {
                var checkPointId = state.checkPoints[i].id.toString();
                var checkPointCoords = [state.checkPoints[i].latitude, state.checkPoints[i].longitude];
                var checkPointPreset = 'islands#blueGovernmentCircleIcon';
                var checkPoint = _checkPoints[checkPointId];
                if (checkPoint) {
                    checkPoint.geometry.setCoordinates(checkPointCoords);
                    checkPoint.options.set('preset', checkPointPreset)
                } else {
                    checkPoint = new ymaps.Placemark(checkPointCoords, {}, {preset: checkPointPreset});
                    _map.geoObjects.add(checkPoint);
                    _checkPoints[checkPointId] = checkPoint;
                }
                checkPointIds.push(checkPointId);
            }
            for (key in _checkPoints) {
                if (_checkPoints.hasOwnProperty(key)) {
                    if (checkPointIds.indexOf(key) === -1) {
                        _map.geoObjects.remove(_checkPoints[key]);
                        delete _checkPoints[key];
                    }
                }
            }
            if (state.players.length == 0) {
                _me.options.set('preset', 'islands#redCircleDotIcon');
            }
            if (state.destination) {
                var destCoords = [state.destination.latitude, state.destination.longitude];
                if (_destination) {
                    _destination.geometry.setCoordinates(destCoords);
                } else {
                    _destination = new ymaps.Placemark(destCoords, {iconContent: 'DZR'}, {preset: 'islands#nightCircleIcon'});
                    _map.geoObjects.add(_destination);
                }
            } else {
                if (_destination) {
                    _map.geoObjects.remove(_destination);
                    _destination = null;
                }
            }
        }
    }
}());

var game = (function() {
    var _myId = null;
    var _myPos = null;
    var _onUpdate = null;
    var _updateTimer = null;
    var _initialized = false;
    var _pin = null;

    var update = function() {
        api.request('/users', 'put', {
            id: _myId,
            lat: _myPos != null ? _myPos[0] : null,
            long: _myPos != null ? _myPos[1] : null
        }).done(function(data) {
            map.showState(data.state, _myId);
        }).fail(function() {
        });
    };

    var connect = function(pin, callbacks) {
        _onUpdate = callbacks.onUpdate;
        api.request('/users', 'post', {
            id: _myId,
            pin: pin,
            lat: _myPos != null ? _myPos[0] : null,
            long: _myPos != null ? _myPos[1] : null
        }).done(function(data) {
            _myId = data.id;
            Cookies.set('uid', data.id, {expires: 7});
            map.showState(data.state, data.id);
            _updateTimer = setInterval(update, appSettings.updateInterval * 1000);
            callbacks.onConnect();
        }).fail(function() {
        });
    };

    return {
        init: function(ymaps, mapId, onInit, onConnect, onUpdate) {
            _pin = Cookies.get('upin');
            _myId = Cookies.get('uid');
            if (!_pin) {
                window.location.href = '/start.html';
                return;
            }
            map.init(ymaps, mapId);
            navigator.geolocation.watchPosition(function (position) {
                _myPos = [position.coords.latitude, position.coords.longitude];
                map.showMe(_myPos);
                if (!_initialized) {
                    connect(_pin, {onConnect: onConnect, onUpdate: onUpdate});
                    onInit();
                    _initialized = true;
                }
            }, function () {}, appSettings.posWatchOptions);
        },
        disconnect: function(onDisconnect) {
            api.request('/users', 'delete', {
                id: _myId
            }).done(function() {
                if (_updateTimer) {
                    clearInterval(_updateTimer);
                }
                _myId = null;
                Cookies.remove('uid');
                Cookies.remove('upin');
                map.showState({players: [], destination: null});
                onDisconnect();
            }).fail(function() {
            })
        },
        getPin: function() {
            return _pin;
        }
    }
}());

var api = (function() {
    return {
        post: function(path, data) {
            return req(path, 'post', data);
        },
        put: function(path, data) {

        },
        request: function(path, method, data) {
            return $.ajax({
                url: appSettings.apiEndpoint + path,
                type: method,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(data)
            });
        }
}
}());

var dashboard = (function() {
    var connect = function(pin) {
        if (!pin) {
            return;
        }
        showBlock('loading');
        game.connect(pin, {
            onConnect: function() {
                $pinSpan.text(pin);
                showBlock('info');
            },
            onUpdate: function() {
            }
        });
    };

    var disconnect = function() {
        showBlock('loading');
        game.disconnect(function() {
            window.location.href = '/start.html';
        });
    };

    var showBlock = function(block) {
        $pinInputBlock.toggle(block == 'input');
        $loadingBlock.toggle(block == 'loading');
        $infoBlock.toggle(block == 'info');
    };

    var $dashboard = $('#dashboard');
    var $pinInputBlock = $('#pin-input-block');
    var $loadingBlock = $('#loading-block');
    var $infoBlock = $('#info-block');
    var $exitBtn = $('#exit-link');
    var $pinSpan = $('#pin-span');

    $exitBtn.on('click', function() {
        disconnect();
    });

    return {
        show: function() {
            $pinSpan.text(game.getPin());
            showBlock('info');
            $dashboard.show();
        }
    }
}());

var start = (function() {
    var $pinEdit = $('#pin-edit');
    var $startBtn = $('#start-btn');

    $pinEdit.keyup(function(e) {
        if (e.keyCode == 13) {
            $startBtn.click();
        }
    });

    $startBtn.on('click', function() {
        var pin = $pinEdit.val();
        if (pin) {
            Cookies.set('upin', pin, {expires: 7});
            window.location.href = '/play.html';
        }
    });

}());

function initGame(ymaps) {
    game.init(ymaps, 'map', function() {}, function() {
        dashboard.show();
    }, function() {

    });
}
