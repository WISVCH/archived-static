var map_center = new google.maps.LatLng(52.0084811, 4.3629584);
var map = new google.maps.Map( document.getElementById("map"),  {
	zoom: 16,
	center: map_center,
	mapTypeId: google.maps.MapTypeId.ROADMAP,
});
 
var marker = new google.maps.Marker({
	position: map_center, 
	map: map, 
	title: 'Symposium',
	icon: '../images/ch_map.png'
});
google.maps.event.addListener(marker, 'click', click);

function click(){

}

var center;
function calculateCenter() {
  center = map.getCenter();
}
google.maps.event.addDomListener(map, 'idle', function() {
  calculateCenter();
});
google.maps.event.addDomListener(window, 'resize', function() {
  map.setCenter(center);
});