/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Export.view.ExportView', {
    extend: 'Ext.Panel',
    xtype: 'exportView',
    layout: 'border',
    defaults: {
        bodyPadding: 2,
        split: true
    },
    items: [{xtype:'exportTopView'},{itemId:'exportCenterViewID',xtype:'exportCenterView'}]
});