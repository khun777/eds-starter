Ext.define('Starter.view.dummy.View', {
	extend: 'Ext.panel.Panel',
	requires: [ 'Starter.view.dummy.Controller', 'Starter.view.dummy.ViewModel' ],
	title: 'Dashboard',
	closable: true,

	controller: {
		xclass: 'Starter.view.dummy.Controller'
	},

	viewModel: {
		xclass: 'Starter.view.dummy.ViewModel'
	},

	layout: {
		type: 'vbox',
		align: 'stretch'
	},

	dockedItems: [{

		xtype: 'toolbar',
		dock: 'top',
		items: [ {
			text: 'Call secured service',
			handler: 'callSecuredService'
		} ]		
	}],
	
	items: [ {
		xtype: 'container',
		height: 250,
		layout: {
			type: 'hbox',
			align: 'stretch'
		},
		items: [ {
			xtype: 'cartesian',
			interactions: 'itemhighlight',
			theme: 'default-gradients',
			height: 250,
			flex: 1,
			margin: '0 0 3 0',
			bind: {
				store: '{companies}'
			},
			axes: [ {
				type: 'numeric',
				position: 'left',
				fields: 'price',
				minimum: 0,
				hidden: true
			}, {
				type: 'category',
				position: 'bottom',
				fields: [ 'name' ],
				label: {
					renderer: function(v) {
						return Ext.String.ellipsis(v, 15, false);
					},
					font: '11px Arial',
					rotate: {
						degrees: -45
					}
				}
			} ],
			series: [ {
				type: 'bar',
				axis: 'left',
				label: {
					display: 'insideEnd',
					field: 'price',
					color: '#000',
					orientation: 'vertical',
					'text-anchor': 'middle'
				},
				xField: 'name',
				yField: 'price'
			} ]

		}, {
			xtype: 'polar',
			theme: 'default-gradients',
			margin: '0 0 0 0',
			width: 200,
			bind: {
				store: '{radar}'
			},

			insetPadding: '15 30 15 30',
			axes: [ {
				type: 'category',
				position: 'angular',
				grid: true,
				label: {
					fontSize: 10
				}
			}, {
				type: 'numeric',
				miniumum: 0,
				maximum: 100,
				majorTickSteps: 5,
				position: 'radial',
				grid: true
			} ],
			series: [ {
				type: 'radar',
				xField: 'Name',
				yField: 'Data',
				showMarkers: true,
				marker: {
					radius: 4,
					size: 4
				},
				style: {
					opacity: 0.5,
					lineWidth: 0.5
				}
			} ]
			
		} ]
	}, {
		xtype: 'container',
		layout: {
			type: 'hbox',
			align: 'stretch'
		},
		flex: 3,
		items: [ {
			xtype: 'grid',
			flex: 6,
			bind: {
				store: '{companies}',
				selection: '{companySelection}'
			},
			defaults: {
				sortable: true
			},
			columns: [ {
				text: i18n.dummy_company,
				flex: 1,
				dataIndex: 'name'
			}, {
				text: i18n.dummy_price,
				width: null,
				dataIndex: 'price',
				formatter: 'usMoney'
			}, {
				text: i18n.dummy_revenue,
				width: null,
				dataIndex: 'revenue',
				renderer: 'percentRenderer'
			}, {
				text: i18n.dummy_growth,
				width: null,
				dataIndex: 'growth',
				renderer: 'percentRenderer',
				hidden: true
			}, {
				text: i18n.dummy_product,
				width: null,
				dataIndex: 'product',
				renderer: 'percentRenderer',
				hidden: true
			}, {
				text: i18n.dummy_market,
				width: null,
				dataIndex: 'market',
				renderer: 'percentRenderer',
				hidden: true
			} ],
			listeners: {
				select: 'onGridSelect'
			}
		}, {
			xtype: 'form',
			flex: 3,
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			margin: '0 0 0 5',
			items: [ {
				margin: '2',
				xtype: 'fieldset',
				flex: 1,
				title: i18n.dummy_nocompany,
				bind: {
					disabled: '{!companySelection}',
					title: '{companySelection.name}'
				},
				defaults: {
					maxValue: 100,
					minValue: 0,
					labelWidth: 90,
					enforceMaxLength: true,
					maxLength: 5,
					bubbleEvents: [ 'change' ]
				},
				defaultType: 'numberfield',
				items: [ {
					fieldLabel: i18n.dummy_price,
					bind: '{companySelection.price}'
				}, {
					fieldLabel: i18n.dummy_revenue + ' %',
					bind: '{companySelection.revenue}'
				}, {
					fieldLabel: i18n.dummy_growth + ' %',
					bind: '{companySelection.growth}'
				}, {
					fieldLabel: i18n.dummy_product + ' %',
					bind: '{companySelection.product}'
				}, {
					fieldLabel: i18n.dummy_market + ' %',
					bind: '{companySelection.market}'
				} ],
				listeners: {
					buffer: 200,
					change: 'formChange'
				}
			} ]
		} ]
	} ]

});