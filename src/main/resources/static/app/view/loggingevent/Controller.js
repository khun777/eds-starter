Ext.define('Starter.view.loggingevent.Controller', {
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
			Starter.Util.successToast(i18n.logevents_deleted);
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
