Ext.define('Start.view.user.Window', {
	extend: 'Ext.window.Window',
	requires: [ 'Start.view.user.WindowController' ],
	layout: 'fit',
	width: 500,
	resizable: true,
	constrain: true,
	modal: true,
	autoShow: true,
	glyph: 0xe803,
	defaultFocus: 'firstName',

	controller: {
		xclass: 'Start.view.user.WindowController'
	},

	listeners : {                       
        afterrender: 'afterrender'
    },
	
	
	items: [ {
		xtype: 'form',
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
			name: 'firstName',
			fieldLabel: i18n.user_firstname,
		// allowBlank: false
		}, {
			name: 'lastName',
			fieldLabel: i18n.user_lastname,
		// allowBlank: false
		}, {
			name: 'email',
			fieldLabel: i18n.user_email,
		// vtype: 'email',
		// allowBlank: false
		}, {
			name: 'newPassword',
			fieldLabel: i18n.user_newpassword,
			inputType: 'password'
		}, {
			name: 'newPasswordRetype',
			fieldLabel: i18n.user_newpasswordretype,
			inputType: 'password'
		}, {
			xtype: 'combobox',
			fieldLabel: i18n.user_language,
			name: 'locale',
			store: [ [ 'de', i18n.user_language_german ], [ 'en', i18n.user_language_english ] ],
			queryMode: 'local',
			emptyText: i18n.user_selectlanguage,
			// allowBlank: false,
			forceSelection: true
		}, {
			fieldLabel: i18n.user_enabled,
			name: 'enabled',
			xtype: 'checkboxfield',
			inputValue: 'true',
			uncheckedValue: 'false'
		}, {
			xtype: 'tagfield',
			fieldLabel: i18n.user_roles,
			store: 'roles',
			name: 'role',
			displayField: 'name',
			valueField: 'name',
			queryMode: 'local',
			forceSelection: true,
			autoSelect: true,
			delimiter: ',',
			editable: false,
			selectOnFocus: false

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
				glyph: 0xe80e,
				handler: 'close'
			} ]
		} ]

	} ]

});