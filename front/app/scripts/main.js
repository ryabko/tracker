function init(ymaps) {
    var map = new ymaps.Map('map', {
        center: [51.661535, 39.200287],
        zoom: 12,
        controls: []
    });

    (function ($) {
        // $(function () {

            console.log('Ready');

            var me = null;
            if (navigator.geolocation) {

                var watchOptions = {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 10000
                };

                navigator.geolocation.watchPosition(function (position) {
                    var lat = position.coords.latitude;
                    var long = position.coords.longitude;
                    console.log('[' + lat + ',' + long + ']');

                    if (me == null) {
                        me = new ymaps.Placemark([lat, long], {}, {preset: 'islands#redCircleDotIcon'});
                        map.geoObjects.add(me);
                        map.setCenter([lat, long]);
                    } else {
                        me.geometry.setCoordinates([lat, long]);
                    }
                }, function () {}, watchOptions);
            }
        // })
    })(jQuery);

}
