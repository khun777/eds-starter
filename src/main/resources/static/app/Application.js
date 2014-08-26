Ext.define('Start.Application', {
	extend: 'Ext.app.Application',
	name: 'Start',

	stores: [
	// TODO: add global / shared stores here
	],

	launch: function() {
		Ext.fly('cssloader').destroy();
		// TODO - Launch the application
	}
});
