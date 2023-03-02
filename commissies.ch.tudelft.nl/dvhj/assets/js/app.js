$(function(){
	$("#new-course").typeahead({ source: function(query, callback){
		$.getJSON(root_url+"Courses/byname/"+query, function(courses){
			callback(courses.map(function(c){ return c.code + " | " + c.name; }));
		});
	} });
	
	$("#new-nominee").typeahead({ source: function(query, callback){
		if(query.indexOf("|") >= 0) return;
		$.getJSON(root_url+"Courses/byteacher/"+query, function(courses){
			callback(courses);
		});
	} });
	
	// Add courses to selection
	function addCourses(courses){
		$table = $("table#courses-visible tbody");
		for(n in courses){
			if($table.find(".course-"+courses[n].id).size() == 0){
				var row = $("<tr><td><a class='delete' href='#'><i class='icon-trash'></i></td><td></td><td></td></a></tr>");
				row.addClass("course-"+courses[n].id).data("course", courses[n]).find("td")
				.first().append("<input type='hidden' class='course-id' name='course[]' value='"+courses[n].id+"' />")
				.next().text(courses[n].code)
				.next().text(courses[n].name)
				$table.append(row);
			}
		}
		update();
	}
	
	function resetCourses(){
		// Remove from table
		$table = $("table#courses-visible tbody");
		$table.find("tr").remove();
		
		// Remove from body class
		$("body").removeClass(($("body").attr("class") || "").split(" ").filter(
			function(el){ return el.indexOf("course-") == 0; 
		}).join(" "));
		
		update();
	}
	
	function update(save){
		if(typeof save == 'undefined') save = true;
		
		// Collect course ids
		var ids = $("table#courses-visible tr").map(function(){
			var id = $(this).find(".course-id").val();
			return typeof id == "undefined" ? null : parseInt(id);
		});
		ids = Array.prototype.slice.call(ids);
		
		updatePills(ids);
		updateTutors(ids);
		updateBar();
		
		// Save
		if(save)
			$.post(root_url+"Student/courses", $("#form-selected-courses").serialize());
		if(save)
			dirty(true);
		
		// Dependent on amount of courses
		$(".course-count").text(ids.length == 0 ? "" : " ("+ids.length+")");
		$(".if-empty").toggle(ids.length == 0);
	}
	update(false);
	
	function updatePills(ids){
		// Make fast index
		var key_ids = {};
		for(var i = ids.length; i--;){
			key_ids[ids[i]] = true;
		}
		if(console) console.log(key_ids);
		$(".tutor .badge").each(function(){
			$(this).toggleClass("badge-info", $(this).attr("data-id") in key_ids);
		});
	}
	
	function updateTutors(ids){
		// Hide and show teachers
		if(ids.length == 0) {
			$(".visible-tutors .tutor").hide();
			$("table#courses-visible tfoot").show();
		} else {
			$(".visible-tutors .tutor").filter(".course-"+ids.join(",.course-")).show();
			$(".visible-tutors .tutor").filter(":not(.course-"+ids.join(",.course-")+")").hide();
			$("table#courses-visible tfoot").hide();
		}
	}
	
	function updateBar(){
		var total = 0, done = 0;
		$(".tutor:visible:not(.disabled)").each(function(){
			total++;
			
			var grade = $(this).find(".grade:not(.placeholdersjs)").val();
			if(grade && parseFloat(grade) >= 8 && $(this).find("textarea:not(.placeholdersjs)").val().length > 3)
				done++;
			else if(grade && parseFloat(grade) < 8)
				done++;
		});
		
		var per = ~~(done/total*100);
		$("form#form-votes input[type=submit]").toggleClass("btn-success", per > 0);
		$(".progress .bar").css("width", per+"%");
	}
	
	// Remove row if deleted
	$("table#courses-visible").on("click", ".delete", function(event){
		$(this).parents("tr").hide(function(){ 
			$(this).remove();
			update();
		});

		event.preventDefault();
		return false;
	});
	
	$("form#form-votes .tutor").on("click", ".disable", function(){
		$(this).parents(".tutor").toggleClass("disabled");
		updateBar();
	});
	
	var isdirty = false;
	function dirty(s){
		isdirty = s;
		if(isdirty)
			$("form#form-votes input[type=submit]").removeAttr("disabled");
		else
			$("form#form-votes input[type=submit]").attr("disabled", 1);
	}
	dirty(false);
	
	// Handle votes
	function saveVotes(event){
		$.post(root_url+"Student/votes", $("form#form-votes").serialize(), function(){
			dirty(false);
			$('#save-confirm').modal('show');
		});
		var stats = $(".tutor").get().map(function(item){
			if($(item).is(":visible") && $(item).find(".grade").val())
				return [
					'_trackEvent',
					'vote',
					$(item).find("h4").first().text(),
					$(item).find(".grade").val()
				];
		}).filter(function(item){ return item; });
		if(_gaq) _gaq.push.apply(_gaq, stats);
		
		// Prevent normal submit
		event.preventDefault();
		return false;
	}
	$("form#form-votes").on("submit", saveVotes);
	
	// Handle searches
	$("form#form-selected-courses").on("submit", function(event){
		var selectors = { 
			'.course-finder-program': "byprogram",
			'.course-finder-name': "byname", 
			'.course-finder-teacher': "byteacher"
		};
		
		for(sel in selectors){
			var val = $(sel).find("input, select").val();
			if(val != "" && val.length > 0){
				(function(sel, val){
					var source = root_url+"Courses/"+selectors[sel]+"/"+val;
					
					// If teacher-search: filter tutor from query and lookup course
					if(sel == ".course-finder-teacher" && val.indexOf(" | ") >= 0){
						val = val.split(" | ");
						source = root_url+"Courses/byname/"+val[1]+" | "+val[2];
					}
					
					$(sel).find("button").attr("disabled","1");
					$.getJSON(source, addCourses).always(function(){
						$(sel).find("button").removeAttr("disabled");
						$(sel).find("input, select").val("").trigger("change");
					});
				})(sel, val);
			}
		}
		
		event.preventDefault();
		return false;
	}).on("reset", function(){ $('#reset-confirm').modal('hide'); resetCourses(); });
	
	$("[data-toggle='tooltip']").tooltip();
	
	$("body").on("keyup change", "input.grade, textarea", function(){ updateBar(); dirty(true); });
	$("form#form-selected-courses").on("keyup change", "select, input", function(){
		if($(this).val() == "")
			$(this).nextAll("button").first().attr("disabled", true);
		else
			$(this).nextAll("button").first().removeAttr("disabled");
	}).find("select, input").trigger("change");
	
	$("input.grade").on("keyup", function(event){
    var val = this.value;

    // Ensure we only handle printable keys, excluding enter and space
    var charCode = event.which;
    if (charCode == 188) {
        var keyChar = String.fromCharCode(charCode);

        // Transform typed character
        var mappedChar = transformTypedChar(keyChar, event);

        var start, end;
        if (typeof this.selectionStart == "number" && typeof this.selectionEnd == "number") {
            // Non-IE browsers and IE 9
						debugger;
            start = this.selectionStart;
            end = this.selectionEnd;
            this.value = val.slice(0, start - 1) + mappedChar + val.slice(end);

            // Move the caret
            this.selectionStart = this.selectionEnd = start + 1;
        } else if (document.selection && document.selection.createRange) {
            // For IE up to version 8
            var selectionRange = document.selection.createRange();
            var textInputRange = this.createTextRange();
            var precedingRange = this.createTextRange();
            var bookmark = selectionRange.getBookmark();
            textInputRange.moveToBookmark(bookmark);
            precedingRange.setEndPoint("EndToStart", textInputRange);
            start = precedingRange.text.length;
            end = start + selectionRange.text.length;

            this.value = val.slice(0, start) + mappedChar + val.slice(end);
            start++;

            // Move the caret
            textInputRange = this.createTextRange();
            textInputRange.collapse(true);
            textInputRange.move("character", start - (this.value.slice(0, start).split("\r\n").length - 1));
            textInputRange.select();
        }

        return false;
    }
	});
	
	$(".jumbotron input.token").on("change, keyup", function(){
		if( $(this).val().length == 0 )
			$(".jumbotron .btn-large").attr("disabled", true);
		else
			$(".jumbotron .btn-large").removeAttr("disabled");
	}).trigger("keyup");
	
	$(window).hashchange( function(){
		if(location.hash == "#login"){
			$(".jumbotron .btn-large").popover('show');
			$(".jumbotron .btn-large, .jumbotron .token").one("click", function(){
				$(".jumbotron .btn-large").popover('hide');
			});
			location.hash = "";
		} 
	});
	$(window).hashchange();
	
	setTimeout(function () {
		var top = ($('.affixed').offset() || {top: 465}).top;
    $('.affixed').affix({
      offset: {
        top: function () { return top; }
      }
    })
  }, 100);
});
function transformTypedChar(charStr, e) {
    return e.which == 188 ? "." : charStr;
}