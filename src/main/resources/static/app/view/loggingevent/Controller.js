Ext.define('Start.view.loggingevent.Controller', {
	extend: 'Ext.app.ViewController',
	
	filterLogLevel: function(cb, selected) {
		if (selected && selected.length === 1) {
			this.getViewModel().set('levelFilter', selected[0].data.level);
		} 
		else {
			this.getViewModel().set('levelFilter', null);
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
