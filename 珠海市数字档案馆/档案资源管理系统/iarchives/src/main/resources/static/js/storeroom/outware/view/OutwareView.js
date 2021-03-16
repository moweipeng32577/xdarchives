/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Outware.view.OutwareView', {
    extend: 'Ext.panel.Panel',
    xtype: 'outwareView',
    layout: 'card',
    activeItem: 0,
    items: [{
        layout: 'border',
        bodyBorder: false,
        defaults: {
            split: true
        },
        itemId: 'gridview',
        items: [{
            region: 'center',
            itemId: 'tabviewID',
            xtype: 'outwareTabView'
        }]
    }]
});