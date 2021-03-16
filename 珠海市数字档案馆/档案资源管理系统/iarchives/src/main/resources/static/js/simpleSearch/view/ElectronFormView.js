/**
 * Created by yl on 2017/11/3.
 */
Ext.define('SimpleSearch.view.ElectronFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'electronFormView',
    itemId: 'electronFormViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'electronFormItemView'
    }]
});