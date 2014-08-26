Ext.define('Start.view.loggingevent.ViewModel', {
	extend: 'Ext.app.ViewModel',
	requires: [ 'Start.model.LoggingEvent' ],

	data: {
		levelFilter: null
	},

	stores: {
		loggingEvents: {
			model: 'Start.model.LoggingEvent',
			autoLoad: true,
			remoteSort: true,
			remoteFilter: true,
			pageSize: 30,
			filters: {
				property: 'level',
				value: '{levelFilter}'
			},
			sorters: [ {
				property: 'dateTime',
				direction: 'DESC'
			} ]
		}
	}

});