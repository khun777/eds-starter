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
			fieldLabel: i18n.login_email,
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
		
		dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'bottom',
		    items: ['->', {
		    	text: 'I forgot my Password',
		    	handler: function() {
		    		loginPanel.hide();		    		
		    		passwordResetPanel.show();
		    		passwordResetPanel.getForm().setValues({email: loginPanel.getValues().username});
		    	}
		    }]
		}, {
		    xtype: 'toolbar',
		    dock: 'bottom',
		    items: ['->',
			    /* <_debug> */
			    {
					text: i18n.login_as_user,
					glyph: 0xe801,
					handler: function() {
						var form = this.up('form').getForm();
						form.setValues({
							username: 'user@start.com',
							password: 'user'
						});
						form.submit();
					}
				}, {
					text: i18n.login_as_admin,
					glyph: 0xe801,
					handler: function() {
						var form = this.up('form').getForm();
						form.setValues({
							username: 'admin@start.com',
							password: 'admin'
						});
						form.submit();
					}
				},/* </debug> */ 
				{
					text: i18n.login,
					glyph: 0xe801,
					handler: function() {
						submitForm();
					}
				}		            
		    ]
		}]		
		
	});

	var passwordResetPanel = new Ext.form.Panel({
		frame: true,
		title: 'Password Reset',
		width: 400,
		hidden: true,
		padding: 5,
		layout: {
			type: 'vbox',
			align: 'stretch'
		},

		items: [{
	      xtype: 'displayfield',
		  value: 'Please enter your email address and we send you an email with<br>a link to a page where you can reset your password.'	
		}, {
			xtype: 'textfield',
			fieldLabel: 'Email',
			name: 'email',
			anchor: '100%'
		}],
		dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'bottom',
		    items: ['->', {
		    	text: 'Send me the link',
		    	handler: function() {
		    		passwordResetPanel.removeAll();		
		    		var di = passwordResetPanel.getDockedItems();
		    		passwordResetPanel.removeDocked(di[0]);
		    		passwordResetPanel.removeDocked(di[1]);
		    		passwordResetPanel.add({
		    			 xtype: 'displayfield',
		    			 value: 'We have sent you an email. Please check your inbox'
		    		});
		    	}
		    }]
		}]
	});
	
	var centerContainer = new Ext.container.Container({
		xtype: 'container',
		region: 'center',
		layout: 'center',
		items: [ {
			xtype: 'container',
			layout: 'vbox',
			width: 400,
			items: [ loginPanel, passwordResetPanel ]
		} ]
	});
	
	new Ext.container.Container({
		plugins: 'viewport',
		style: {
			backgroundColor: 'white'
		},
		layout: {
			type: 'border',
			padding: 5
		},
		items: [ headerContainer, centerContainer ]
	});

	loginPanel.getForm().findField('username').focus();

	Ext.fly('cssloader').destroy();
	if (location.search === '?error') {
		Ext.toast(i18n.login_failed, i18n.error, 't');
	}

});