Ext.define('Starter.view.main.MainController', {
	extend: 'Ext.app.ViewController',
	requires: [ 'Starter.view.usersettings.Window' ],

	init: function() {
		securityService.getLoggedOnUser(this.afterLoggedOnUserReceived, this);
	},

	onNavigationStoreLoad: function(store) {
		if (this.autoOpenView) {
			var rec = store.findRecord('view', this.autoOpenView);
			if (rec) {
				this.onNavigationTreeSelectionchange(null, [rec]);
			}
		}
	},
	
	afterLoggedOnUserReceived: function(user) {
		this.getViewModel().set('loggedOnUser', user.firstName + ' ' + user.lastName);
		this.autoOpenView = user.autoOpenView;
	},

	onNavigationTreeSelectionchange: function(tree, selections) {
		if (selections && selections.length === 1) {
			var selected = selections[0];
			var view = selected.data.view;
			var tabPanel = this.lookupReference('centerTabPanel');

			if (view) {
				var tab = tabPanel.child('panel[navigationId=' + selected.getId() + ']');
				if (!tab) {
					var viewObject = Ext.create(view, {
						icon: selected.data.icon,
						treePath: this.getPath(selected),
						navigationId: selected.getId()
					});
					tab = tabPanel.add(viewObject);
				}
				this.activeTab = tab;
				tabPanel.setActiveTab(tab);
			}
		}
	},

	getPath: function(node) {
		return node.parentNode ? this.getPath(node.parentNode) + "/" + node.getId() : "/" + node.getId();
	},

	onTabChange: function(tabPanel, newCard) {
		if (!this.activeTab || this.activeTab.getId() !== newCard.getId()) {
			this.activeTab = newCard;
			var navigationTree = this.lookupReference('navigationTree');
			navigationTree.suspendEvents();
			navigationTree.selectPath(newCard.treePath);
			navigationTree.resumeEvents();
		}		
	},

	onTabRemove: function(tabPanel) {		
		var navigationTree = this.lookupReference('navigationTree');
		if (tabPanel.items.length === 0) {
			navigationTree.getSelectionModel().deselectAll();
		}
		this.activeTab = null;
	},

	onUserSettingsClick: function() {
		new Starter.view.usersettings.Window();
	}

});
