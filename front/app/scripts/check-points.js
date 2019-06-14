var api = (function () {
    return {
        request: function (path, method, data) {
            return $.ajax({
                url: appSettings.apiEndpoint + path,
                type: method,
                dataType: 'text',
                contentType: 'plain/text',
                data: data
            });
        }
    }
}());

var checkpoints = (function () {
    var load = function () {
        api.request('/check-points', 'get')
            .done(function (data) {
                $checkPoints.val(data);
            })
            .fail(function () {
                alert('Ошибка');
            });
    };

    var save = function () {
        api.request('/check-points', 'put', $checkPoints.val())
            .done(function () {
                alert('Сохранено');
                window.location.href = '/check-points.html';
            })
            .fail(function (e) {
                alert('Ошибка');
            });
    };

    var $checkPoints = $('#check-points');
    var $saveButton = $('#save');

    $saveButton.on('click', function () {
        save();
    });

    return {
        load: load
    }
}());

function init() {
    checkpoints.load();
}
