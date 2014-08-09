Ext.define('Start.view.main.MainController', {
	extend: 'Ext.app.ViewController',

	onClickButton: function() {
		Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
	},

	onConfirm: function(choice) {
		if (choice === 'yes') {
			//
		}
	}
});
