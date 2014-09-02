Ext.define('Starter.view.dummy.Controller', {
	extend: 'Ext.app.ViewController',

	percentRenderer: function perc(v) {
		return v + '%';
	},

	onGridSelect: function(grid, record) {

		this.getStore('radar').loadData([ {
			'Name': i18n.dummy_price,
			'Data': record.get('price')
		}, {
			'Name': i18n.dummy_revenue + ' %',
			'Data': record.get('revenue')
		}, {
			'Name': i18n.dummy_growth + ' %',
			'Data': record.get('growth')
		}, {
			'Name': i18n.dummy_product + ' %',
			'Data': record.get('product')
		}, {
			'Name': i18n.dummy_market + ' %',
			'Data': record.get('market')
		} ]);
	},

	formChange: function(field, newValue, oldValue, listener) {
		var companySelection = this.getViewModel().get('companySelection');
		this.onGridSelect(null, companySelection);
	},
	
	callSecuredService: function() {
		console.log('dummyService.notAllowedTest'); 
		dummyService.notAllowedTest(function(flag) {
			console.log('call to dummyService.notAllowedTest successful', flag);
		});
	}

});
