/**
 * Created by Administrator on 2019/5/17.
 */


Ext.define('SimpleSearch.view.ApplyPrintView', {
    extend: 'Ext.panel.Panel',
    xtype: 'applyPrintView',
    itemId: 'applyPrintViewId',
    region: 'center',
    autoScroll: true,
    items:[{
        xtype:'applyPrintFormView'
    }]
});
