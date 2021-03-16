/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ClassifySearch.view.ClassifyLookAddFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'classifylookAddFormView',
    itemId: 'classifylookAddFormViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'classifylookAddFormItemView'
    }]
});