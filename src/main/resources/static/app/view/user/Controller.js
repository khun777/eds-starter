Ext.define('Starter.view.user.Controller', {
	extend: 'Ext.app.ViewController',
	requires: [ 'Starter.view.user.Window', 'Starter.store.Roles' ],

	init: function() {
		new Starter.store.Roles();
	},

	onFilterSpecialKey: function(tf, e) {
		if (e.getKey() === e.ENTER) {
			this.onFilter(tf);
		}
	},

	onFilter: function(tf, trigger) {
		if (trigger && trigger.id === 'clear') {
			tf.setValue('');
		}

		var value = tf.getValue();
		var store = this.getStore('users');
		if (value) {
			this.getViewModel().set('filter', value);
			store.filter('filter', value);
		}
		else {
			this.getViewModel().set('filter', null);
			store.clearFilter();
		}
	},

	onItemDoubleClick: function(grid, record) {
		this.editUser(i18n.user_edit, this.getViewModel().get('selectedUser'));
	},

	newUser: function() {
		this.getViewModel().set('selectedUser', null);
		this.editUser(i18n.user_new, new Starter.model.User());
	},

	editUser: function(title, record) {
		var editWin = new Starter.view.user.Window({
			title: title
		});
		this.getView().add(editWin);		
		editWin.show();
		
		var form = editWin.down('form');
		form.loadRecord(record);
		form.isValid();
	},

	destroyUser: function(record) {
		Ext.Msg.confirm(i18n.attention, Ext.String.format(i18n.destroyConfirmMsg, record.get('email')), function(choice) {
			if (choice === 'yes') {
				record.erase({
					callback: function(records, operation, success) {
						if (success) {
							Starter.Util.successToast(i18n.destroysuccessful);
							this.getViewModel().set('selectedUser', null);
							this.getStore('users').reload();
						}
						else {
							Starter.Util.errorToast(i18n.servererror);
						}
					},
					scope: this
				});
			}
		}, this);
	},

	switchTo: function(record) {
		if (record) {
			securityService.switchUser(record.getId(), function(ok) {
				if (ok) {
					window.location.reload();
				}
			}, this);
		}
	},
	
	unlock: function(record) {
		if (record) {
			userService.unlock(record.getId(), function(success) {
				if (success) {
					record.set('lockedOutUntil', null, {
						dirty: false
					});
				}
			});
		}
	},

	onItemContextMenu: function(view, record, item, index, e, eOpts) {
		e.stopEvent();
		this.showContextMenu(record, e.getXY());
	},

	onActionColumnClick: function(grid, rowIndex, colIndex, item, e, record, row) {
		this.showContextMenu(record, null, row);
	},

	showContextMenu: function(record, xy, item) {
		var items = this.buildContextMenuItems(record);

		if (this.actionMenu) {
			this.actionMenu.destroy();
		}

		this.actionMenu = new Ext.menu.Menu({
			items: items,
			border: true
		});

		if (xy) {
			this.actionMenu.showAt(xy);
		}
		else {
			this.actionMenu.showBy(item);
		}
	},

	buildContextMenuItems: function(record) {
		return [ {
			text: i18n.edit,
			glyph: 0xe803,
			handler: this.editUser.bind(this, i18n.user_edit, record)
		}, {
			text: i18n.user_unlock,
			hidden: !record.data.lockedOutUntil,
			handler: this.unlock.bind(this, record)
		}, {
			text: i18n.destroy,
			glyph: 0xe806,
			handler: this.destroyUser.bind(this, record)
		}, {
			xtype: 'menuseparator'
		}, {
			text: i18n.user_switchto,
			handler: this.switchTo.bind(this, record)
		} ];
	}

});