Ext.define('Starter.view.user.WindowController', {
	extend: 'Ext.app.ViewController',

	init: function() {

	},

	afterrender: function(window, options) {
		this.keyNav = new Ext.util.KeyMap({
			target: window.el,
			binding: [ {
				key: "s",
				ctrl: true,
				defaultEventAction: 'preventDefault',
				fn: function() {
					this.save();
				},
				scope: this
			} ]
		});
	},

	close: function() {
		this.getView().close();
	},

	save: function() {
		var form = this.getView().down('form').getForm();

		if (form.isValid()) {
			var record = form.getRecord().copy();
			form.updateRecord(record);

			record.save({
				callback: function(r, op) {
					if (op.success) {
						Starter.Util.successToast(i18n.savesuccessful);
						this.getStore('users').reload();
						this.close();
					}
					else {
						Starter.Util.errorToast(i18n.inputcontainserrors);
						if (op.getResponse() && op.getResponse().result && op.getResponse().result.validations) {
							op.getResponse().result.validations.forEach(function(validation) {
								var field = form.findField(validation.field);
								field.markInvalid(validation.message);
							});
						}
					}
				},
				scope: this
			});

		}

	}

});