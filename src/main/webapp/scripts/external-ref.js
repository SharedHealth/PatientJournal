function showServiceProvider(anchor, element_ref_class) {
   return function(data) {
     var element = anchor.next();
     element.html(data);
     element.show();
     anchor.hide();
   }
}

function onError(data){
    if(data.status === 401){
        window.location.reload();
    }
}

function fetchExternal(event) {
   event.preventDefault();
   var external_ref = encodeURI(event.target.getAttribute('href').replace("1.0//","1.0/"));
   var element_ref_class = event.target.getAttribute("data-ref-element-class");
   var anchor = $(this);

    $.ajax({
        url: '/external?ref=' + external_ref,
        type: 'GET',
        success: showServiceProvider(anchor, element_ref_class),
        error: onError
    });
}