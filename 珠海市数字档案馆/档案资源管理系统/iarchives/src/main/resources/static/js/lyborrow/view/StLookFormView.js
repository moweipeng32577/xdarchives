/**
 * Created by Administrator on 2019/2/19.
 */

Ext.define('Borrow.view.StLookFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'stLookFormView',
    itemId: 'stLookFormViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'stLookAddFormView'
    }]
});
