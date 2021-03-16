/**
 * Created by yl on 2017/11/3.
 */
Ext.define('SimpleSearch.view.LookAddFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'lookAddFormView',
    itemId: 'lookAddFormViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'lookAddFormItemView'
    }]
});