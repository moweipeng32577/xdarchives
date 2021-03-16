/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ClassifySearch.view.ClassifyElectronFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'classifyElectronFormView',
    itemId: 'classifyElectronFormViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'classifyElectronFormItemView'
    }]
});