Ext.define('Start.view.accesslog.TabPanel', {
	extend: 'Ext.tab.Panel',
	requires: [ 'Start.view.accesslog.Grid', 'Start.view.accesslog.OsGraph', 'Start.view.accesslog.UaGraph',
	            'Start.view.accesslog.Controller', 'Start.view.accesslog.ViewModel'],

	controller: {
		xclass: 'Start.view.accesslog.Controller'
	},

	viewModel: {
		xclass: 'Start.view.accesslog.ViewModel'
	},		
	
	title: i18n.accesslog,
	closable: true,
	border: false,
	
	padding: '2 0 0 0',

	listeners: {
		tabchange: 'onTabChange'
	},
	
	items: [{
		xclass: 'Start.view.accesslog.Grid'
	}, {
		xclass: 'Start.view.accesslog.UaGraph'
	}, {
		xclass: 'Start.view.accesslog.OsGraph'
	}]
	
//	initComponent: function() {
//		this.items = [ {
//			xtype: 'accesslog'
//		}/*, {
//			xtype: 'uagraph'
//		}, {
//			xtype: 'osgraph'
//		}*/ ];
//		this.callParent(arguments);
//	}

});
