var divBegin = '<div>';
var divEnd = '</div>';
var defaultPatterns = {
	'Error': 'text-danger',
	'Debug': 'text-info',
	'Exception': 'text-warning'
};
var logDiv = $('#logDiv');
var logDetailDivTemplate =
	'<div role="tabpanel" class="tab-pane fade" id="toreplace1">toreplace2</div>';
var tabTemplate =
	'<li role="presentation"><a href="#toreplace1" aria-controls="toreplace1" role="tab" data-toggle="tab">toreplace1</a></li>';
var socketioOnTemplate =
	'socket.on("toreplace1",function(data){addNewLine(data,logDetailArrays["toreplace1"])});'
var logDetailArrays = {};
var socket;

$(document).ready(function() {
	socket = io.connect('http://localhost:9092');

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
	var configJSON = JSON.parse(pText);
	loadTabAndContent(configJSON);
	initOnMessage(configJSON);
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
		var $divToAppend = $(logDetailDivTemplate.replace(/toreplace1/g, filename).replace(
			/toreplace2/, 'Reading ' + config[filename]));
		$myContent.append($divToAppend);
		logDetailArrays[filename] = $divToAppend;
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
	var coloredLine = colorLine(line);
	if (!toElement) toElement = logDiv;
	toElement.append(turnToJqueryObjet(coloredLine));
};

function colorLine(line) {
	var patterns = readPattern();
	for (var pattern in patterns) {
		line = divBegin + line.replace(new RegExp(pattern, 'ig'), '<font class="' +
			defaultPatterns[pattern] + '">' + pattern + '</font>') + divEnd;
	}
	return line;
};

function turnToJqueryObjet(whoami) {
	if (whoami instanceof jQuery) {
		return whoami;
	}
	return $(whoami);
};