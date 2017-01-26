var map = (function() {
    var _map = null;
    var _me = null;
    var _otherPlayers = {};
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
                    console.log('Changing coordinates for ' + id);
                    mark.geometry.setCoordinates(coords);
                    mark.options.set('preset', preset)
                } else {
                    console.log('Adding coordinates for ' + id);
                    mark = new ymaps.Placemark(coords, {}, {preset: preset});
                    _map.geoObjects.add(mark);
                    _otherPlayers[id] = mark;
                }
                ids.push(id);
            }
            for (var key in _otherPlayers) {
                if (_otherPlayers.hasOwnProperty(key)) {
                    if (ids.indexOf(key) == -1) {
                        console.log('Deleting coordinates for ' + key);
                        _map.geoObjects.remove(_otherPlayers[key]);
                        delete _otherPlayers[key];
                    }
                }
            }
            if (state.players.length == 0) {
                _me.options.set('preset', 'islands#redCircleDotIcon');
            }
            if (state.destination) {
                var destCoords = [state.destination.latitude, state.destination.longitude];
                if (_destination) {
                    console.log('Updating destination');
                    _destination.geometry.setCoordinates(destCoords);
                } else {
                    console.log('Creating destination');
                    _destination = new ymaps.Placemark(destCoords, {}, {preset: 'islands#blueFamilyCircleIcon'});
                    _map.geoObjects.add(_destination);
                }
            } else {
                if (_destination) {
                    console.log('Removing destination');
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

    var update = function() {
        console.log('update');
        api.request('/users', 'put', {
            id: _myId,
            lat: _myPos != null ? _myPos[0] : null,
            long: _myPos != null ? _myPos[1] : null
        }).done(function(data) {
            console.log('Updated');
            console.log(data);
            map.showState(data.state, _myId);
        }).fail(function() {
            console.log('Updating error');
        });
    };

    return {
        init: function(ymaps, mapId, onInit) {
            map.init(ymaps, mapId);
            navigator.geolocation.watchPosition(function (position) {
                _myPos = [position.coords.latitude, position.coords.longitude];
                console.log('my position: ' + _myPos);
                map.showMe(_myPos);
                if (!_initialized) {
                    onInit();
                    _initialized = true;
                }
            }, function () {}, appSettings.posWatchOptions);
        },
        connect: function(pin, callbacks) {
            _onUpdate = callbacks.onUpdate;
            console.log('Game connecting.....');
            api.request('/users', 'post', {
                pin: pin,
                lat: _myPos != null ? _myPos[0] : null,
                long: _myPos != null ? _myPos[1] : null
            }).done(function(data) {
                console.log('Game connected');
                console.log(data);
                _myId = data.id;
                Cookies.set('uid', data.id, {expires: 7});
                map.showState(data.state, data.id);
                _updateTimer = setInterval(update, appSettings.updateInterval * 1000);
                callbacks.onConnect();
            }).fail(function() {
                console.log('Game connection error');
            });
        },
        disconnect: function(onDisconnect) {
            console.log('Game disconnecting....');
            api.request('/users', 'delete', {
                id: _myId
            }).done(function() {
                if (_updateTimer) {
                    clearInterval(_updateTimer);
                }
                _myId = null;
                Cookies.remove('uid');
                map.showState({players: [], destination: null});
                onDisconnect();
            }).fail(function() {
                console.log('Disconnecting error');
            })
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
        showBlock('loading');
        game.connect(pin, {
            onConnect: function() {
                $pinSpan.text(pin);
                showBlock('info');
            },
            onUpdate: function() {
                console.log('Update callback');
            }
        });
    };

    var disconnect = function() {
        showBlock('loading');
        game.disconnect(function() {
            $pinSpan.text('');
            $pinEdit.val('');
            showBlock('input');
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
    var $pinEdit = $('#pin-edit');
    var $startBtn = $('#start-btn');
    var $exitBtn = $('#exit-btn');
    var $pinSpan = $('#pin-span');

    $startBtn.on('click', function() {
        connect($pinEdit.val());
    });

    $pinEdit.keyup(function(e) {
        if (e.keyCode == 13) {
            $startBtn.click();
        }
    });

    $exitBtn.on('click', function() {
        disconnect();
    });

    return {
        show: function() {
            $dashboard.show();
        }
    }
}());

function initGame(ymaps) {
    game.init(ymaps, 'map', function() {
        dashboard.show();
    });
}
