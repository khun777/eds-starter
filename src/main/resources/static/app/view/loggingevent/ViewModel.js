Ext.define('Start.view.loggingevent.ViewModel', {
	extend: 'Ext.app.ViewModel',
	requires: [ 'Start.model.LoggingEvent' ],

	stores: {
		loggingEvents: {
			model: 'Start.model.LoggingEvent',
			autoLoad: true,
			remoteSort: true,
			remoteFilter: true,
			pageSize: 30,
			sorters: [ {
				property: 'dateTime',
				direction: 'DESC'
			} ]
		}
	}

});