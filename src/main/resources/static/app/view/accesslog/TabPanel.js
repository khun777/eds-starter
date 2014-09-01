Ext.define('Starter.view.accesslog.TabPanel', {
	extend: 'Ext.tab.Panel',
	requires: [ 'Starter.view.accesslog.Grid', 'Starter.view.accesslog.OsGraph', 'Starter.view.accesslog.UaGraph', 'Starter.view.accesslog.Controller', 'Starter.view.accesslog.ViewModel' ],

	controller: {
		xclass: 'Starter.view.accesslog.Controller'
	},

	viewModel: {
		xclass: 'Starter.view.accesslog.ViewModel'
	},

	title: i18n.navigation_system_accesslog,
	closable: true,
	border: false,

	padding: '2 0 0 0',

	listeners: {
		tabchange: 'onTabChange'
	},

	items: [ {
		xclass: 'Starter.view.accesslog.Grid'
	}, {
		xclass: 'Starter.view.accesslog.UaGraph'
	}, {
		xclass: 'Starter.view.accesslog.OsGraph'
	} ]

});
