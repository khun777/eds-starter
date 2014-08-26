Ext.define('Start.Application', {
	extend: 'Ext.app.Application',
	name: 'Start',

	stores: [
	// TODO: add global / shared stores here
	],

	constructor: function() {
		Ext.setGlyphFontFamily('custom');
		
		var heartbeat = new Ext.direct.PollingProvider({
			type: 'polling',
			interval: 5 * 60 * 1000, // 5 minutes
			url: Ext.app.POLLING_URLS.heartbeat
		});
		Ext.direct.Manager.addProvider(Ext.app.REMOTING_API, heartbeat);
		//this.setupGlobalErrorHandler();

//		Ext.direct.Manager.on('event', function(e) {
//			if (e.code && e.code === 'parse') {
//				window.location.reload();
//			}
//		});
//
//		Ext.direct.Manager.on('exception', function(e) {
//			if (e.message === 'accessdenied') {
//				Ext.toast(i18n.error_accessdenied, i18n.error, 't');
//			} else {
//				Ext.toast(e.message, i18n.error, 't');
//			}
//		});
		
		
		this.callParent(arguments);
	},
	
	launch: function() {
		Ext.fly('cssloader').destroy();
	},
	
	setupGlobalErrorHandler: function() {
		var existingFn = window.onerror;
		if (typeof existingFn === 'function') {
			window.onerror = Ext.Function.createSequence(existingFn, this.globalErrorHandler);
		} else {
			window.onerror = this.globalErrorHandler;
		}
	},

	globalErrorHandler: function(msg, url, line) {
		console.log(msg, line);
		var message = msg + "-->" + url + "::" + line;
		logService.error(message);
	}
	
});
