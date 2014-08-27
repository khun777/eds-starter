Ext.define('Start.view.accesslog.UaGraph', {
	extend: 'Ext.panel.Panel',
	
	layout: {
		type: 'fit'
	},

	title: i18n.accesslog_userAgents,
	
	items: [{
        xtype: 'cartesian',
        insetPadding: 10,
        bind: {
        	store: '{accessLogUserAgents}'
        },
        legend: {
            docked: 'bottom'
        },
        axes: [
            {
                type: 'category',
                fields: [
                    'yearMonth'
                ],
                position: 'bottom'
            },
            {
                type: 'numeric',
                fields: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],               
                position: 'left',
                minimum: 0,
                maximum: 100,
                majorTickSteps: 10,
                renderer: function (v) { return v + ' %'; }                
            }
        ],
        series: [
            {
                type: 'bar',
                stacked: true,
                xField: 'yearMonth',
                //title: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],
                yField: [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ],
            
            
            axis: 'left',
            
            tooltip: {
                trackMouse: true,
                style: 'background: #fff',
                renderer: function (storeItem, item) {
                    this.setHtml(item.field + ' : ' + storeItem.get(item.field) + ' %');
                }
            }            
            
            }
        ]
    }],
	
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
	
//	initComponent: function() {
//		var me = this;
//
//		var fields = [ 'IE', 'Chrome', 'Firefox', 'Safari', 'Opera', 'Other' ];
//
//		me.items = [ {
//			xtype: 'chart',
//			itemId: 'uaChart',
//			animate: true,
//			defaultInsets: 30,
//			store: Ext.create('E4ds.store.AccessLogUserAgents'),
//			legend: {
//				position: 'right'
//			},
//			axes: [ {
//				type: 'Numeric',
//				position: 'left',
//				fields: fields,
//				title: i18n.accesslog_userAgent_usage,
//				grid: true,
//				decimals: 0,
//				minimum: 0,
//				maximum: 100
//			}, {
//				type: 'Category',
//				position: 'bottom',
//				fields: [ 'yearMonth' ],
//				title: i18n.accesslog_userAgent_month
//			} ],
//			series: [ {
//				type: 'area',
//				axis: 'left',
//				highlight: true,
//				tips: {
//					trackMouse: true,
//					renderer: function(storeItem, item) {
//						var d = storeItem.get('yearMonth'), percent = storeItem
//								.get(item.storeField)
//								+ '%';
//						this.setTitle(item.storeField + ' - ' + d + ' - ' + percent);
//					}
//				},
//				xField: 'name',
//				yField: fields,
//				style: {
//					lineWidth: 1,
//					stroke: '#666',
//					opacity: 0.86
//				}
//			} ]
//		} ];
//
//
//
//		me.callParent(arguments);
//	}

});