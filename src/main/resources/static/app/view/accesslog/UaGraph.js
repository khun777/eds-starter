Ext.define('Starter.view.accesslog.UaGraph', {
	extend: 'Ext.panel.Panel',

	layout: {
		type: 'fit'
	},

	title: i18n.accesslog_userAgents,

	items: [ {
		xtype: 'cartesian',
		theme: 'default-gradients',
		insetPadding: 10,
		bind: {
			store: '{accessLogUserAgents}'
		},
		legend: {
			docked: 'bottom'
		},
		axes: [ {
			type: 'category',
			fields: [ 'yearMonth' ],
			position: 'bottom'
		}, {
			type: 'numeric',
			fields: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],
			position: 'left',
			minimum: 0,
			maximum: 100,
			majorTickSteps: 10,
			renderer: function(v) {
				return v + ' %';
			}
		} ],
		series: [ {
			type: 'bar',
			stacked: true,
			xField: 'yearMonth',
			// title: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],
			yField: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],

			axis: 'left',

			tooltip: {
				trackMouse: true,
				style: 'background: #fff',
				renderer: function(storeItem, item) {
					this.setHtml(item.field + ' : ' + storeItem.get(item.field) + ' %');
				}
			}

		} ]
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
			reference: 'uaYearsCombobox',
			valueField: 'year',
			displayField: 'year',
			queryMode: 'local',
			forceSelection: true,
			listeners: {
				change: 'onUaYearsCBChange'
			}
		} ]
	} ]

});