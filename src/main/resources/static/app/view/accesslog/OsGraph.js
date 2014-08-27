Ext.define('Start.view.accesslog.OsGraph', {
	extend: 'Ext.panel.Panel',

	layout: {
		type: 'fit'
	},

	title: i18n.accesslog_operatingSystems,

	items: [ {
		xtype: 'polar',
		theme: 'default-gradients',
		insetPadding: 10,
		innerPadding: 10,
		bind: {
			store: '{accessLogOs}'
		},
		series: [ {
			type: 'pie',
			label: {
				field: 'name',
				display: 'rotate'
			},
			xField: 'value'
		} ],
		series: [ {
			type: 'pie',
			angleField: 'value',
			label: {
				field: 'name',
				calloutLine: {
					length: 60,
					width: 3
				}
			},
			highlight: true,
			tooltip: {
				trackMouse: true,
				renderer: function(storeItem, item) {
					this.setHtml(storeItem.get('name') + ': ' + storeItem.get('percent')
							+ ' %');
				}
			}
		} ],
		interactions: [ 'rotate', 'itemhighlight' ],
	} ],

	dockedItems: [ {
		xtype: 'toolbar',
		dock: 'top',
		items: [ {
			xtype: 'combobox',
			fieldLabel: i18n.accesslog_userAgent_year,
			allowBlank: false,
			labelWidth: 40,
			bind: {
				store: '{accessLogYears}'
			},
			reference: 'osYearsCombobox',
			valueField: 'year',
			displayField: 'year',
			queryMode: 'local',
			forceSelection: true,
			listeners: {
				change: 'onOsYearsCBChange'
			}
		} ]
	} ]

});