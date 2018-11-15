$(function() {

	//header
	var myElement = document.querySelector("header");
	var headroom  = new Headroom(myElement);
	headroom.init();

	// anchor
	$(".cover .anchor").click(function () {

		var anchorPos = $(".anchor-scroll").offset().top;

		$("html, body").animate({
			scrollTop: anchorPos
		}, 1000);

	});

});
