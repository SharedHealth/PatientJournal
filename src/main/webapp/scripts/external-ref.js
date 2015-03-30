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

function fetchExternal(event) {
   event.preventDefault();
   var external_ref = encodeURI(event.target.getAttribute('href').replace("1.0//","1.0/"));
   var element_ref_class = event.target.getAttribute("data-ref-element-class");
   var anchor = $(this);
   if(element_ref_class == "serviceProvider")
        $.get( "/external?ref="+external_ref, showServiceProvider(anchor, element_ref_class));
   else if(element_ref_class == "patientDetails")
        $.get( "/external?ref="+external_ref, showPatientDetails(anchor, element_ref_class));
}