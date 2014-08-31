Ext.define('Start.view.user.ViewModel', {
	extend: 'Ext.app.ViewModel',
	requires: [ 'Start.model.User', 'Start.model.Role' ],

	data: {
		selectedUser: null
	},

	stores: {
		users: {
			model: 'Start.model.User',
			autoLoad: true,
			remoteSort: true,
			remoteFilter: true,
			pageSize: 30,
			sorters: [ {
				property: 'lastName',
				direction: 'ASC'
			} ]
		}		
	}

});