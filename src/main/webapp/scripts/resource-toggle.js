function toggleResourceContents () {
    $span = $(this);
    $content = $span.next().next();
    $content.slideToggle(500, function () {
        $span.text(function () {
            return $content.is(":visible") ? "-" : "+";
        });
    });
};