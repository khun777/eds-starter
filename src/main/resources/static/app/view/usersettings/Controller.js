Ext.define('Starter.view.usersettings.Controller', {
	extend: 'Ext.app.ViewController',

	init: function() {
		userService.readSettings(function(user) {
			this.getViewModel().set('user', user);
		}, this);
	},

	save: function() {
		var vm = this.getViewModel();
		var form = this.lookupReference('userSettingsForm').getForm();
		userService.updateSettings(vm.get('user'), function(validations) {
			if (validations.length > 0) {
				Starter.Util.errorToast(i18n.inputcontainserrors);

				validations.forEach(function(validation) {
					var field = form.findField(validation.field);
					field.markInvalid(validation.message);
				});
			}
			else {
				Starter.Util.successToast(i18n.savesuccessful);
				this.closeWindow();
			}
		}, this);
	},

	closeWindow: function() {
		this.getView().close();
	}

});