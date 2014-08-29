Ext.define('Start.view.usersettings.Controller', {
	extend: 'Ext.app.ViewController',

	init: function() {
		userSettingsService.read(function(user) {
			this.getViewModel().set('user', user);
		}, this);
	},

	save: function() {
		var vm = this.getViewModel();
		var form = this.lookupReference('userSettingsForm').getForm();
		userSettingsService.updateSettings(vm.get('user'), function(validations) {
			if (validations.length > 0) {
				Ext.toast({
					html: i18n.inputcontainserrors,
					title: i18n.error,
					align: 't',
					shadow: true,
					width: 200,
					slideInDuration: 100,
					hideDuration: 100,
					bodyStyle: {
						background: 'red',
						color: 'white',
						textAlign: 'center'
					}
				});
				
				validations.forEach(function(validation) {
					var field = form.findField(validation.field);
					field.markInvalid(validation.message);
				});
			} else {
				Ext.toast({
					html: i18n.savesuccessful,
					title: i18n.successful,
					align: 't',
					shadow: true,
					width: 200,
					slideInDuration: 100,
					hideDuration: 100,
					bodyStyle: {
						background: 'lime',
						textAlign: 'center'
					}
				});				
				this.closeWindow();
			}
		}, this);
	},

	closeWindow: function() {
		this.getView().close();
	}

});