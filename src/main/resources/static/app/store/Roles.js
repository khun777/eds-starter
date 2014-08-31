Ext.define('Start.store.Roles', {
	extend: 'Ext.data.Store',
	model: 'Start.model.Role',
	storeId: 'roles',
	autoLoad: true,
	remoteFilter: false,
	remoteSort: false,
	pageSize: 0
});