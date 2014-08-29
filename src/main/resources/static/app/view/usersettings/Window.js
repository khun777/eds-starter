Ext.define('Start.view.usersettings.Window', {
	extend: 'Ext.window.Window',
	requires: [ 'Start.view.usersettings.Controller' ],

	controller: {
		xclass: 'Start.view.usersettings.Controller'
	},

	title: i18n.user_settings,
	width: 500,
	layout: 'fit',
	resizable: true,
	constrain: true,
	autoShow: true,
	modal: true,
	glyph: 0xe803,
	defaultFocus: 'firstName',
	ghost: false,

	viewModel: {},

	items: [ {
		xtype: 'form',
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
			//allowBlank: false
		}, {
			bind: '{user.lastName}',
			name: 'lastName',
			fieldLabel: i18n.user_lastname,
			//allowBlank: false
		}, {
			bind: '{user.email}',
			name: 'email',
			fieldLabel: i18n.user_email,
			//vtype: 'email',
			//allowBlank: false
		}, {
			xtype: 'combobox',
			fieldLabel: i18n.user_language,
			bind: {
				value: '{user.locale}'
			},
			store: [ [ 'de', i18n.user_language_german ], [ 'en', i18n.user_language_english ] ],
			queryMode: 'local',
			emptyText: i18n.user_selectlanguage,
			//allowBlank: false,
			name: 'locale',
			forceSelection: true
		}, {
			xtype: 'label',
			html: '<hr />'
		}, {
			bind: '{user.currentPassword}',
			name: 'currentPassword',
			fieldLabel: i18n.user_currentpassword,
			inputType: 'password'
		}, {
			bind: '{user.newPassword}',
			name: 'newPassword',
			fieldLabel: i18n.user_newpassword,
			inputType: 'password'
		}, {
			bind: '{user.newPasswordRetype}',
			name: 'newPasswordRetype',
			fieldLabel: i18n.user_newpasswordretype,
			inputType: 'password',
//			validator: function() {
//				var newPassword = this.up('window').getViewModel().get('user.newPassword');
//				var newPasswordRetype = this.getValue();
//				if ((Ext.isEmpty(newPassword) && Ext.isEmpty(newPasswordRetype)) || (newPassword === newPasswordRetype)) {
//					return true;
//				}
//				return i18n.user_pwdonotmatch;
//			}
		} ],

		dockedItems: [ {
			xtype: 'toolbar',
			dock: 'bottom',
			items: [ '->', {
				xtype: 'button',
				text: i18n.save,
				action: 'save',
				glyph: 0xe80d,
				formBind: true,
				handler: 'save'
			}, {
				text: i18n.close,
				handler: 'closeWindow',
				glyph: 0xe80e
			} ]
		} ]

	} ]

});
