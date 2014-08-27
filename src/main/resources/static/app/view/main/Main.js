Ext.define('Start.view.main.Main', {
	extend: 'Ext.container.Container',
	requires: [ 'Start.view.main.MainController', 'Start.view.main.MainModel',
			'Start.view.main.SideBar', 'Start.view.main.Header' ],

	controller: {
		xclass: 'Start.view.main.MainController'
	},

	viewModel: {
		xclass: 'Start.view.main.MainModel'
	},

	layout: {
		type: 'border',
		padding: 3
	},

	items: [ {
		xclass: 'Start.view.main.SideBar',
		region: 'west',
		reference: 'navigationTree',
		split: true
	}, {
		xclass: 'Start.view.main.Header',
		region: 'north',
		split: false
	}, {
		xtype: 'tabpanel',
		region: 'center',
		reference: 'centerTabPanel',
		split: true,
		listeners: {
			tabchange: 'onTabChange',
			remove: 'onTabRemove'
		}
	} ]
});
