var divBegin = '<div>';
var divEnd = '</div>';
var defaultPatterns = {
	'Error': 'text-danger',
	'Debug': 'text-info',
	'Exception': 'text-warning'
};
var logDiv = $('#logDiv');

$(document).ready(function() {
	var socket = io.connect('http://localhost:9092');

	socket.on('connect', function() {
		addNewLine('Client connect to server');
	})

	socket.on('message', function(data) {
		addNewLine(data);
	});

	socket.on('disconnect', function() {
		addNewLine('Client disconnect from server');
	})
});

function readPattern(argument) {
	// read a json from a no display p
	// TODO
	return defaultPatterns;
};

function addNewLine(line) {
	// we set a default pattern
	var coloredLine = colorLine(line);
	logDiv.append(turnToJqueryObjet(coloredLine));
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