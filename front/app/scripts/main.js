function initMe(ymaps) {
    var map = new ymaps.Map('map', appSettings.mapOptions);

    (function () {
            var me = null;
            if (navigator.geolocation) {
                navigator.geolocation.watchPosition(function (position) {
                    var coords = [position.coords.latitude, position.coords.longitude];

                    if (me == null) {
                        me = new ymaps.Placemark(coords, {}, {preset: 'islands#redCircleDotIcon'});
                        map.geoObjects.add(me);
                        map.setCenter(coords);
                    } else {
                        me.geometry.setCoordinates(coords);
                    }
                }, function () {}, appSettings.posWatchOptions);
            }
    })();

}
