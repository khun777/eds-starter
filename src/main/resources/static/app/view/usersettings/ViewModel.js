Ext.define('Starter.view.usersettings.ViewModel', {
	extend: 'Ext.app.ViewModel',
	requires: [ 'Starter.model.AccessLog' ],

	stores: {
		last10logs: {
			model: 'Starter.model.AccessLog',
			autoLoad: false,
			remoteSort: false,
			remoteFilter: false,
			pageSize: 0,
			sorters: [ {
				property: 'loginTimestamp',
				direction: 'DESC'
			} ],
			proxy: {
				type: "direct",
				directFn: "accessLogService.last10Logs"
			}
		}
	}

});