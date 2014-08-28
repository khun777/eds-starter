Ext.define('Start.view.loggingevent.Controller', {
	extend: 'Ext.app.ViewController',

	filterLogLevel: function(cb, newValue) {
		var store = this.getStore('loggingEvents');
		if (newValue) {
			this.getViewModel().set('levelFilter', newValue);
			store.filter('level', newValue);
		}
		else {
			this.getViewModel().set('levelFilter', null);
			store.clearFilter();
		}
	},

	deleteAll: function() {
		var filter = this.getView().getStore().filters.get(0);
		loggingEventService.deleteAll(filter && filter.value, function() {
			Ext.toast(i18n.logevents_deleted, i18n.successful, 't');
			this.getStore('loggingEvents').load();
		}, this);
	}

	/* <debug> */
	,
	addTestData: function() {
		loggingEventService.addTestData(function() {
			this.getStore('loggingEvents').load();
		}, this);
	}
/* </debug> */

});
