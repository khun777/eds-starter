Ext.onReady(function() {
	Ext.setGlyphFontFamily('custom');

	var headerContainer, loginPanel;
	var capslockwarningText = '<div style="font-weight: bold;">' + i18n.login_capslockwarning_title + '</div><br />' + '<div>'
			+ i18n.login_capslockwarning_line1 + '</div><br />' + '<div>' + i18n.login_capslockwarning_line2 + '</div>';

	headerContainer = new Ext.container.Container({
		border: '0 0 2 0',
		style: {
		    borderColor: 'black',
		    borderStyle: 'solid'
		},
		region: 'north',
		height: 35,
		layout: {
			type: 'hbox',
			align: 'stretch'
		},

		items: [ {
			html: i18n.app_title,
			cls: 'appHeader'
		} ]
	});

	function submitForm() {
		var form = loginPanel.getForm();
		if (form.isValid()) {
			form.submit();
		}
	}

	loginPanel = new Ext.form.Panel({
		frame: true,
		title: i18n.login_title,
		url: 'login.html',
		width: 400,
		padding: 5,
		glyph: 0xe812,

		standardSubmit: true,

		defaults: {
			anchor: '100%'
		},

		defaultType: 'textfield',

		fieldDefaults: {
			msgTarget: 'side'
		},

		items: [ {
			fieldLabel: i18n.login_username,
			name: 'username',
			allowBlank: false,
			listeners: {
				specialkey: function(field, e) {
					if (e.getKey() === e.ENTER) {
						submitForm();
					}
				}
			}
		}, {
			fieldLabel: i18n.login_password,
			name: 'password',
			inputType: 'password',
			validateOnBlur: false,
			allowBlank: false,
			enableKeyEvents: true,
			listeners: {
				specialkey: function(field, e) {
					if (e.getKey() === e.ENTER) {
						submitForm();
					}
				},
				render: {
					fn: function(field, eOpts) {
						field.capsWarningTooltip = Ext.create('Ext.tip.ToolTip', {
							target: field.bodyEl,
							anchor: 'top',
							width: 305,
							html: capslockwarningText
						});
						field.capsWarningTooltip.disable();
					},
					scope: this
				},
				keypress: {
					fn: function(field, e, eOpts) {
						var charCode = e.getCharCode();
						if ((e.shiftKey && charCode >= 97 && charCode <= 122) || (!e.shiftKey && charCode >= 65 && charCode <= 90)) {
							field.capsWarningTooltip.enable();
							field.capsWarningTooltip.show();
						} else {
							if (field.capsWarningTooltip.hidden === false) {
								field.capsWarningTooltip.disable();
								field.capsWarningTooltip.hide();
							}
						}
					},
					scope: this
				},
				blur: function(field) {
					if (field.capsWarningTooltip.hidden === false) {
						field.capsWarningTooltip.hide();
					}
				}
			}
		}, {
			fieldLabel: i18n.login_rememberme,
			name: 'remember-me',
			xtype: 'checkbox'
		} ],

		buttons: [ /* <_debug> */{
			text: i18n.login_withuser,
			glyph: 0xe801,
			handler: function() {
				var form = this.up('form').getForm();
				form.setValues({
					username: 'user',
					password: 'user'
				});
				form.submit();
			}
		}, {
			text: i18n.login_withadmin,
			glyph: 0xe801,
			handler: function() {
				var form = this.up('form').getForm();
				form.setValues({
					username: 'admin',
					password: 'admin'
				});
				form.submit();
			}
		},/* </debug> */{
			text: i18n.login,
			glyph: 0xe801,
			handler: function() {
				submitForm();
			}
		} ]
	});

	var centerContainer = new Ext.container.Container({
		xtype: 'container',
		region: 'center',
		layout:'center',
		items: loginPanel
	});
	
	new Ext.container.Container({
		plugins: 'viewport',
		style: {
			backgroundColor: 'rgb(225, 225, 225)'
		},
		layout: {
			type: 'border',
			padding: 5
		},
		items: [ headerContainer, centerContainer ]
	});

	loginPanel.getForm().findField('username').focus();


});