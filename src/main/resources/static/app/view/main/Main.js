Ext.define('Start.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: ['Start.view.main.MainController', 'Start.view.main.MainModel'],
    
    controller: {
    	xtype: 'Start.view.main.MainController'
    },
    
    viewModel: {
        xtype: 'Start.view.main.MainModel'
    },

    layout: {
        type: 'border'
    },

    items: [{
        xtype: 'panel',
        bind: {
            title: '{name}'
        },
        region: 'west',
        html: '<ul><li>This area is commonly used for navigation, '+'for example, using a "tree" component.</li></ul>',
        width: 250,
        split: true,
        tbar: [{
            text: 'Button',
            handler: 'onClickButton'
        }]
    },{
        region: 'center',
        xtype: 'tabpanel',
        items:[{
            title: 'Tab 1',
            html: '<h2>Content appropriate for the current navigation.</h2>'
        }]
    }]
});
