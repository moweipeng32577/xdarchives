/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Restore.view.RestoreView', {
    extend: 'Ext.panel.Panel',
    xtype: 'restoreView',
    layout: 'border',

    items: [{
        region: 'center',
        xtype: 'restoreGridView'
    },{
        region: 'east',
        width: XD.treeWidth,
        xtype: 'treepanel',
        itemId: 'treepanelId',
        store: 'RetoreTreeStore',
        collapsible: true,
        split: 1,
        header: false,
        bodyBorder: false,
        buttons: [
            { text: '恢复',itemId:'restore'},
            { text: '关闭',itemId:'close'}
        ]
    }]
});