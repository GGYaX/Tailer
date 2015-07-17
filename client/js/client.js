var divBegin = '<div>';
var divEnd = '</div>';
var defaultPatterns = {
	'Error': 'text-danger',
	'Debug': 'text-info',
	'Exception': 'text-warning'
};
var logDiv = $('#logDiv');
var logDetailPTemplate = '<p style="display:none;" id="toreplace1LineNumber">0</p>';
var logDetailDivTemplate =
	'<div role="tabpanel" class="tab-pane fade" id="toreplace1">toreplace2</div>';
var logDetailTemplate = logDetailPTemplate + logDetailDivTemplate;
var tabTemplate =
	'<li role="presentation"><a href="#toreplace1" aria-controls="toreplace1" role="tab" data-toggle="tab">toreplace1</a></li>';
var socketioOnTemplate =
	'socket.on("toreplace1",function(data){addNewLine(data,logDetailArrays["toreplace1"])});'
var logDetailArrays = {};
var socket;
var atBottom = true;
var entityMap = {
	"&": "&amp;",
	"<": "&lt;",
	">": "&gt;",
	'"': '&quot;',
	"'": '&#39;',
	"/": '&#x2F;'
};


$(window).scroll(function() {
	if ($(window).scrollTop() + $(window).height() == $(document).height()) {
		atBottom = true;
	} else {
		atBottom = false;
	}
});

$(document).ready(function() {
	var url = window.location.href;
	var domain = (url.split('/')[2]).split(':')[0];

	socket = io.connect('http://' + domain + ':9092');

	socket.on('connect', function() {
		addNewLine('Client connect to server');
	});

	socket.on('init', function(data) {
		addToConfg(data);
		init();
	});

	socket.on('disconnect', function() {
		addNewLine('Client disconnect from server');
	});
});

function addToConfg(config) {
	$('#tabConfig').text(config);
}

function init() {
	// initTab();
	// initContent();
	var pText = $('#tabConfig').text();
	var filesToTail = JSON.parse(pText);
	loadTabAndContent(filesToTail);
	initOnMessage(filesToTail);
}

function initTab(argument) {
	var initialTab =
		'<ul class="nav nav-tabs" role="tablist" id="myTab"><li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab">Home</a></li></ul>';
	var $tab = $('#myTab');
	$tab.replaceWith(initialTab);
}

function initContent(argument) {
	var initialCotent =
		'<div class="tab-content" id="myContent"><div role="tabpanel" class="tab-pane fade in active" id="home"><div class="row"><div class="col-lg-12" id="logDiv"></div></div></div></div>';
	var $content = $('#myContent');
	$content.replaceWith(initialCotent);
}

function loadTabAndContent(config) {
	// parse p element

	var $myTab = $('#myTab');
	var $myContent = $('#myContent');
	for (var filename in config) {
		var $tabToAppend = $(tabTemplate.replace(/toreplace1/g, filename));
		$myTab.append($tabToAppend);
		var $templateToAppend = $(logDetailTemplate.replace(/toreplace1/g, filename).replace(
			/toreplace2/, 'Reading ' + config[filename]));
		$myContent.append($templateToAppend);
		$divContent = $('[id="'+ filename +'"]');
		logDetailArrays[filename] = $divContent;
	}
}

function initOnMessage(config) {
	var events = [];
	for (var filename in config) {
		events.push(filename);
	}
	for (var i in events)
		(function(e) {
			socket.on(e, function(data) {
				addNewLine(data, logDetailArrays[e]);
			});
		})(events[i]);
}

function readPattern(argument) {
	// read a json from a no display p
	// TODO
	return defaultPatterns;
};

function addNewLine(line, toElement) {

	// we set a default pattern
	var coloredLine = colorLine(escapeHtml(line));
	if (!toElement) toElement = logDiv;
	var LineNumber = parseInt(toElement.prev().text());
	if (LineNumber >= 500) {
		// remove the first element inside
		toElement.find(':first-child').remove();
	}
	plusLineNumber(toElement.prev());
	toElement.append(turnToJqueryObjet(coloredLine));
	if (atBottom) gotoBottom();
};

function plusLineNumber(element) {
	var LineNumber = parseInt(element.text());
	LineNumber ++;
	element.text(LineNumber);
}

function minusLineNumber (element) {
	var LineNumber = parseInt(element.text());
	LineNumber --;
	element.text(LineNumber);
}

function colorLine(line) {
	var patterns = readPattern();
	for (var pattern in patterns) {
		line = line.replace(new RegExp(pattern, 'ig'), '<font class="' +
			defaultPatterns[pattern] + '">' + pattern + '</font>');
	}
	return divBegin + line + divEnd;
};

function turnToJqueryObjet(whoami) {
	if (whoami instanceof jQuery) {
		return whoami;
	}
	return $(whoami);
};

function gotoBottom() {
	$(window).scrollTop($(document).height());
}

function escapeHtml(string) {
return String(string).replace(/[&<>"'\/]/g, function (s) {
  return entityMap[s];
});
}