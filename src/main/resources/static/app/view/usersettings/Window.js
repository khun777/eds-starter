Ext.define('Starter.view.usersettings.Window', {
	extend: 'Ext.window.Window',
	requires: [ 'Starter.view.usersettings.Controller', 'Starter.view.usersettings.ViewModel' ],
	stateId: 'Starter.view.usersettings.Window',
	stateful: true,

	controller: {
		xclass: 'Starter.view.usersettings.Controller'
	},

	viewModel: {
		xclass: 'Starter.view.usersettings.ViewModel'
	},

	title: i18n.user_settings,
	width: 500,
	layout: 'fit',
	resizable: true,
	constrain: true,
	autoShow: false,
	modal: true,
	glyph: 0xe803,
	defaultFocus: 'firstName',
	ghost: false,

	items: [ {
		xtype: 'tabpanel',
		plain: true,
		margin: 3,
		items: [ {
			xtype: 'form',
			title: i18n.user_personal,
			reference: 'userSettingsForm',
			padding: 5,
			bodyPadding: 10,

			defaultType: 'textfield',
			defaults: {
				anchor: '100%'
			},

			fieldDefaults: {
				msgTarget: 'side'
			},

			items: [ {
				itemId: 'firstName',
				bind: '{user.firstName}',
				name: 'firstName',
				fieldLabel: i18n.user_firstname,
				allowBlank: false
			}, {
				bind: '{user.lastName}',
				name: 'lastName',
				fieldLabel: i18n.user_lastname,
				allowBlank: false
			}, {
				bind: '{user.email}',
				name: 'email',
				fieldLabel: i18n.user_email,
				vtype: 'email',
				allowBlank: false
			}, {
				xtype: 'combobox',
				fieldLabel: i18n.user_language,
				bind: {
					value: '{user.locale}'
				},
				store: [ [ 'de', i18n.user_language_german ], [ 'en', i18n.user_language_english ] ],
				queryMode: 'local',
				emptyText: i18n.user_selectlanguage,
				allowBlank: false,
				name: 'locale',
				forceSelection: true
			}, {
				xtype: 'checkbox',
				bind: {
					value: '{user.passwordReset}'
				},
				inputValue: 'true',
				uncheckedValue: 'false',
				labelWidth: 320,
				fieldLabel: i18n.user_passwordreset
			} ],

			dockedItems: [ {
				xtype: 'toolbar',
				dock: 'bottom',
				items: [ '->', {
					xtype: 'button',
					text: i18n.save,
					glyph: 0xe80d,
					formBind: true,
					handler: 'save'
				}, {
					text: i18n.close,
					handler: 'closeWindow',
					glyph: 0xe80e
				} ]
			} ]

		}, {
			xtype: 'grid',
			title: i18n.user_lastlogins,
			bind: '{last10logs}',
			stateId: 'Starter.view.usersettings.Window.Last10Logs',
			stateful: true,
			columns: [ {
				text: i18n.accesslog_ip,
				dataIndex: 'ipAddress',
				width: 120
			}, {
				text: i18n.accesslog_location,
				dataIndex: 'location',
				width: 100
			}, {
				text: i18n.accesslog_userAgent,
				dataIndex: 'userAgentName',
				width: 100
			}, {
				text: i18n.accesslog_userAgentVersion,
				dataIndex: 'userAgentVersion',
				width: 80
			}, {
				text: i18n.accesslog_operatingSystem,
				dataIndex: 'operatingSystem',
				width: 100
			}, {
				text: i18n.accesslog_login,
				dataIndex: 'loginTimestamp',
				width: 150,
				xtype: 'datecolumn',
				format: 'Y-m-d H:i:s'
			} ]
		}, {
			xtype: 'panel',
			title: i18n.user_settings_clear_state_title,
			bodyPadding: 20,
			layout: {
				type: 'vbox'
			},
			items: [ {
				xtype: 'button',
				handler: 'clearState',
				ui: 'default-toolbar',
				text: i18n.user_settings_clear_state
			} ]
		} ]
	} ]

});
