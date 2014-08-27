Ext.define('Start.view.config.Form', {
	extend: 'Ext.form.Panel',
	requires: [ 'Start.view.config.Controller' ],

	controller: {
		xclass: 'Start.view.config.Controller'
	},

	viewModel: {
		data: {
			testEmailButtonEnabled: false
		}
	},
	layout: 'vbox',

	title: i18n.config,
	closable: true,

	fieldDefaults: {
		msgTarget: 'side'
	},

	bodyPadding: 10,

	items: [ {
		xtype: 'fieldset',
		title: i18n.config_logging,
		collapsible: false,
		items: [ {
			xtype: 'combobox',
			fieldLabel: i18n.config_loglevel,
			name: 'logLevel',
			bind: {
				store: '{logLevels}',
			},
			valueField: 'level',
			displayField: 'level',
			queryMode: 'local',
			forceSelection: true,
			value: 'ERROR'
		} ]
	}, {
		xtype: 'fieldset',
		title: i18n.config_smtp,
		collapsible: false,
		defaultType: 'textfield',
		defaults: {
			width: 500
		},
		items: [ {
			fieldLabel: i18n.config_sender,
			name: 'sender',
			allowBlank: false,
			vtype: 'email',
			maxLength: 1024,
			enforceMaxLength: true
		}, {
			fieldLabel: i18n.config_server,
			name: 'server',
			allowBlank: false,
			maxLength: 1024,
			enforceMaxLength: true
		}, {
			xtype: 'numberfield',
			fieldLabel: i18n.config_port,
			name: 'port',
			allowBlank: false,
			width: 200,
			minValue: 1,
			maxValue: 65535
		}, {
			fieldLabel: i18n.config_username,
			name: 'username',
			allowBlank: true,
			maxLength: 1024,
			enforceMaxLength: true
		}, {
			fieldLabel: i18n.config_password,
			name: 'password',
			allowBlank: true,
			maxLength: 1024,
			enforceMaxLength: true
		} ]
	}, {
		xtype: 'container',
		layout: 'hbox',
		items: [ {
			xtype: 'button',
			text: i18n.save,
			glyph: 0xe80d,
			formBind: true,
			handler: 'onSaveButtonClick'
		}, {
			xtype: 'tbseparator',
			width: 100
		}, {
			xtype: 'fieldcontainer',
			layout: 'hbox',
			items: [ {
				xtype: 'textfield',
				bind: {
					value: '{testEmailReceiver}'
				},
				emptyText: i18n.config_testReceiver,				
				listeners: {
					change: 'onTestEmailReceiverChange'
				}

			}, {
				xtype: 'button',
				text: i18n.config_sendTestEmail,
				glyph: 0xe800,
				bind: {
					disabled: '{!testEmailButtonEnabled}'
				},
				columnWidth: 0.4,
				margin: '0 0 0 10',
				handler: 'onSendTestEmailClick'
			} ]
		} ]
	} ]
});