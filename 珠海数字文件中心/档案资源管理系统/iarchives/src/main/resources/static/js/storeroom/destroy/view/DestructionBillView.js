/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Destroy.view.DestructionBillView', {
    extend: 'Ext.panel.Panel',
    xtype: 'destructionBillView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },
    items: [
        {
            width: 240,
            minWidth: 240,
            title: '(按下Ctrl+F可查找)',
            header:false,
            region: 'west',
            floatable: false,
            layout: 'fit',
            items: [{xtype: 'destructionBillTreeView'}]
        },
        {
            collapsible: false,
            region: 'center',
            layout: 'fit',
            items: [{xtype: 'destructionBillGridView'}]
        }
    ]
});