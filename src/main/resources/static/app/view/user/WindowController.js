Ext.define('Start.view.user.WindowController', {
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
						this.getStore('users').reload();
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
						this.close();
					}
					else {
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