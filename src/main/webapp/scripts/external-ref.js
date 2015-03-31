function showServiceProvider(anchor, element_ref_class) {
   return function(data) {
     var div = anchor.parent().find("."+element_ref_class);
     div.html(data);
     div.show();
     anchor.hide();
   }
}

function showPatientDetails(anchor, element_ref_class) {
   return function(data) {
     var div = anchor.parent().parent().find("."+element_ref_class);
     div.html(data);
     div.show();
     anchor.parent().hide();
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
   var onSuccess;
   if(element_ref_class == "serviceProvider")
        onSuccess = showServiceProvider(anchor, element_ref_class);
   else if(element_ref_class == "patientDetails")
        onSuccess = showPatientDetails(anchor, element_ref_class);

    $.ajax({
        url: '/external?ref=' + external_ref,
        type: 'GET',
        success: onSuccess,
        error: onError
    });
}