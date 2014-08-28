Ext.define('Start.view.config.Controller', {
	extend: 'Ext.app.ViewController',

	init: function() {
		var form = this.getView().getForm();
		appConfigurationService.read(function(result) {
			form.setValues(result);
			form.isValid();
		});
	},

	onSaveButtonClick: function() {
		var form = this.getView().getForm();
		appConfigurationService.save(form.getFieldValues(), function() {
			Ext.toast(i18n.config_saved, i18n.successful, 't');
		});
	},

	onTestEmailReceiverChange: function(tf, newValue) {
		if (!Ext.isEmpty(newValue) && Ext.form.field.VTypes.email(newValue)) {
			this.getViewModel().set('testEmailButtonEnabled', true);
		}
		else {
			this.getViewModel().set('testEmailButtonEnabled', false);
		}
	},

	onSendTestEmailClick: function() {
		var testReceiver = this.getViewModel().get('testEmailReceiver');
		appConfigurationService.sendTestEmail(testReceiver, function() {
			Ext.toast(i18n.config_testEmailsent, i18n.successful, 't');
		});
	}
});
