function fetchExternal(event) {
   event.preventDefault();
   var external_ref = encodeURI(event.target.getAttribute('href').replace("1.0//","1.0/"));
   var element_ref_id = event.target.getAttribute("data-ref-element-id");
   $.get( "/external?ref="+external_ref, function( data ) {
       $("#"+element_ref_id).html(data);
       $("#"+element_ref_id).show();
   });
}