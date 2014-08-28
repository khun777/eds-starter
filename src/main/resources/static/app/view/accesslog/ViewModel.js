Ext.define('Start.view.accesslog.ViewModel', {
	extend: 'Ext.app.ViewModel',
	requires: [ 'Start.model.AccessLog' ],

	stores: {
		accessLogs: {
			model: 'Start.model.AccessLog',
			autoLoad: true,
			remoteSort: true,
			remoteFilter: true,
			pageSize: 30,
			sorters: [ {
				property: 'logIn',
				direction: 'DESC'
			} ]
		},
		accessLogOs: {
			autoLoad: false,
			remoteSort: false,
			remoteFilter: false,
			pageSize: 0,
			fields: [ {
				name: "name",
				type: "string"
			}, {
				name: 'value',
				type: 'float'
			} ],

			proxy: {
				type: 'direct',
				directFn: 'accessLogService.readOsStats'
			}
		},
		accessLogUserAgents: {
			autoLoad: false,
			remoteSort: false,
			remoteFilter: false,
			pageSize: 0,
			fields: [ {
				name: "yearMonth",
				type: "string"
			}, {
				name: 'IE',
				type: 'float'
			}, {
				name: 'Chrome',
				type: 'float'
			}, {
				name: 'Firefox',
				type: 'float'
			}, {
				name: 'Safari',
				type: 'float'
			}, {
				name: 'Opera',
				type: 'float'
			}, {
				name: 'Other',
				type: 'float'
			} ],

			proxy: {
				type: 'direct',
				directFn: 'accessLogService.readUserAgentsStats'
			}
		},
		accessLogYears: {
			autoLoad: true,
			remoteSort: false,
			remoteFilter: false,
			pageSize: 0,

			fields: [ {
				name: "year",
				type: "int"
			} ],

			sorters: [ {
				property: 'year',
				direction: 'DESC'
			} ],

			proxy: {
				type: 'direct',
				directFn: 'accessLogService.readAccessLogYears'
			}
		}
	}

});