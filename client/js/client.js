var divBegin = '<div>';
var divEnd = '</div>';

$(document).ready(function() {
	var socket = io.connect('http://localhost:9092');
	var container = $('#container');

	socket.on('connect', function() {
		// TODO 
		var onlineMsg = $(divBegin + 'Ready to read' + divEnd);
		container.append(onlineMsg);
	})

	socket.on('message', function(data) {
		var newLine = $(divBegin + data + divEnd);
		container.append(newLine);
	});

	socket.on('disconnect', function() {

	})
});