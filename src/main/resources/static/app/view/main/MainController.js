Ext.define('Start.view.main.MainController', {
	extend: 'Ext.app.ViewController',
	
	init: function() {		
		securityService.getLoggedOnUser(this.afterLoggedOnUserReceived, this);
	},

	afterLoggedOnUserReceived: function(user) {
		this.getViewModel().set('loggedOnUser', user.firstName + ' ' + user.name);
	}

});
