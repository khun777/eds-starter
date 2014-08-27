Ext.define('Start.view.accesslog.Grid', {
	extend: 'Ext.grid.Panel',

	title: i18n.accesslog_log,
	bind: '{accessLogs}',
	
	columns: [ {
		text: i18n.accesslog_username,
		dataIndex: 'userName',
		width: 200
	}, {
		text: i18n.accesslog_userAgent,
		dataIndex: 'userAgentName',
		flex: 1
	}, {
		text: i18n.accesslog_userAgentVersion,
		dataIndex: 'userAgentVersion',
		width: 100
	}, {
		text: i18n.accesslog_operatingSystem,
		dataIndex: 'operatingSystem',
		width: 200
	}, {
		text: i18n.accesslog_login,
		dataIndex: 'logIn',
		width: 150,
		xtype: 'datecolumn',
		format: 'Y-m-d H:i:s'
	}, {
		text: i18n.accesslog_logout,
		dataIndex: 'logOut',
		width: 150,
		xtype: 'datecolumn',
		format: 'Y-m-d H:i:s'
	}, {
		text: i18n.accesslog_duration,
		dataIndex: 'duration',
		width: 200,
		sortable: false
	} ],

	dockedItems: [ {
		xtype: 'toolbar',
		dock: 'top',
		items: [ {
			text: i18n.accesslog_deleteall,
			glyph: 0xe806,
			handler: 'deleteAll'
		}, /* <debug> */'-', {
			text: i18n.accesslog_testinsert,
			glyph: 0xe807,
			handler: 'addTestData'
		}, /* </debug> */'->', {
			fieldLabel: i18n.accesslog_username,
			xtype: 'textfield',
			triggers: {
		      search: {
		        cls: Ext.baseCSSPrefix + 'form-search-trigger',
		        handler: 'onUsernameFilter'
		      },
			  clear: {
			    type: 'clear',
				hideWhenEmpty: false,
				handler: 'onUsernameFilter'
		      }		      
			},
			listeners: {
				specialKey: 'onUsernameFilterSpecialKey'
			}
		} ]
	}, {
		xtype: 'pagingtoolbar',
		dock: 'bottom',
		bind: {
	      store: '{accessLogs}'
		}
	} ]

});