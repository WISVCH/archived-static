$(function() {
	$("#element-animation").click(function() {
		$(".element-animation").toggleClass("element-animation-active");
	
	 setTimeout(function () {
       window.location.href = "http://wisv.ch/rally"; //will redirect to your blog page (an ex: blog.html)
    }, 700);
	});
});