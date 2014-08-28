Ext.define('Start.view.accesslog.Controller', {
	extend: 'Ext.app.ViewController',

	onUserFilterSpecialKey: function(tf, e) {
		if (e.getKey() === e.ENTER) {
			this.onUserFilter(tf);
		}
	},

	onUserFilter: function(tf, trigger) {
		if (trigger && trigger.id === 'clear') {
			tf.setValue('');
		}

		var value = tf.getValue();
		var store = this.getStore('accessLogs');
		if (value) {
			store.filter('filter', value);
		}
		else {
			store.clearFilter();
		}
	},

	deleteAll: function() {
		accessLogService.deleteAll(function() {
			Ext.toast(i18n.accesslog_deleted, i18n.successful, 't');
			this.getStore('accessLogs').load();
			this.getStore('accessLogYears').load();
		}, this);
	},

	onUaYearsCBChange: function(cb, newValue) {
		this.getStore('accessLogUserAgents').load({
			params: {
				queryYear: newValue
			}
		});
	},

	onOsYearsCBChange: function(cb, newValue) {
		this.getStore('accessLogOs').load({
			params: {
				queryYear: newValue
			}
		});
	},

	onTabChange: function(tabPanel, newCard) {
		var accessLogYearsStore = this.getStore('accessLogYears');
		if (accessLogYearsStore.getCount() > 0) {
			if (newCard.xclass === 'Start.view.accesslog.UaGraph') {
				Ext.Function.defer(function() {
					this.lookupReference('uaYearsCombobox').select(accessLogYearsStore.first());
				}, 1, this);
			}
			else if (newCard.xclass === 'Start.view.accesslog.OsGraph') {
				Ext.Function.defer(function() {
					this.lookupReference('osYearsCombobox').select(accessLogYearsStore.first());
				}, 1, this);
			}
		}
	}

	/* <debug> */
	,
	addTestData: function() {
		accessLogService.addTestData(function() {
			this.getStore('accessLogs').load();
			this.getStore('accessLogYears').load();
		}, this);
	}
/* </debug> */

});
