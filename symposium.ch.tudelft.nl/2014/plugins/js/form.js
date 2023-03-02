$( document ).ready(function() {
	$(".extern-info").hide("fast");
	$(".student-info").hide("fast");
	$("#otherstudy").hide("fast");
	$('input:text[name="otherstudy"]').hide("fast");
	$(".price").hide("fast");
	$("#price").text("€5,-");
	$('input:radio[name="type"]').change(function(){
		$(".price").show("fast");
		type = $(this).val();
		if($(this).val() == 0){
			$(".student-info").show("fast");
			$("#price").text("€5,-");
		}
		else{
			$(".student-info").hide("fast");
		}
		if($(this).val() == 3){
			$(".extern-info").show("fast");
			$("#price").text("€80,-");
		}
		else{
			$(".extern-info").hide("fast");
		}
		
		if($(this).val() == 2){
			$("#price").text("€25,-");
		}
		
		if($(this).val() == 1){
			$("#price").text("€25,-");
		}
	});
	
	$('input:radio[name="study"]').change(function(){
		study = $(this).val();
		if($(this).val() == 2){
			$('input:text[name="otherstudy"]').show("fast");
		}
		else{
			$('input:text[name="otherstudy"]').hide("fast");
		}
	});
});

function validateEmail(email){
	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function validateForm(){
	var correct = true;
	if (!$('input:radio[name="gender"]:checked').length){
		correct = false;
		$("#sex").addClass("required");
	}
	else{
		$("#sex").removeClass("required");
	}
	
	if ($('input:text[name="firstname"]').val() == ""){
		correct = false;
		$("#firstname").addClass("required");
	}
	else{
		$("#firstname").removeClass("required");
	}
	
	if ($('input:text[name="surname"]').val() == ""){
		correct = false;
		$("#surname").addClass("required");
	}
	else{
		$("#surname").removeClass("required");
	}
	
	if(!validateEmail($('input:text[name="email"]').val())){
		correct = false;
		$("#email").addClass("required");
	}
	else{
		$("#email").removeClass("required");
	}
	
	if (!$('input:radio[name="type"]:checked').length){
		correct = false;
		$("#type").addClass("required");
	}
	else{
		$("#type").removeClass("required");
	}
	
	//student
	if (type == 0){
		if (!$('input:radio[name="study"]:checked').length){
			correct = false;
			$("#study").addClass("required");
		}
		else{
			$("#study").removeClass("required");
		}
	
		if (study == 2){
			if ($('input:text[name="otherstudy"]').val() == ""){
				correct = false;
				$("#study").addClass("required");
			}
			else{
				$("#study").removeClass("required");
			}
		}
		
		if (!$('input:radio[name="progress"]:checked').length){
			correct = false;
			$("#studyprogress").addClass("required");
		}
		else{
			$("#studyprogress").removeClass("required");
		}
	}
	
	if (type == 3){
		if ($('input:text[name="company"]').val() == ""){
				correct = false;
				$("#company").addClass("required");
			}
			else{
				$("#company").removeClass("required");
			}
	}	
	return correct;
}
var study;
var type;