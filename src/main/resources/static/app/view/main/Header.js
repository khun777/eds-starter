Ext.define('Start.view.main.Header', {
	extend: 'Ext.container.Container',

	height: 35,
	layout: {
		type: 'hbox',
		align: 'stretch'
	},

	items: [ {
		html: i18n.app_title,
		cls: 'appHeader'
	}, {
		xtype: 'tbspacer',
		flex: 1
	}, {
		xtype: 'button',
		margin: '2 0 5 0',
		ui: 'default-toolbar',
		bind: {
			text: '{loggedOnUser}'
		},
		glyph: 0xe809
	}, {
		xtype: 'button',
		margin: '2 0 5 5',
		ui: 'default-toolbar',
		text: i18n.logout,
		glyph: 0xe802,
		href: 'logout',
		hrefTarget: '_self'
	} ]

});