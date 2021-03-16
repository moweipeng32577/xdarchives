/**
 * Created by tanly on 2018/1/23 0023.
 */
Ext.define('OAImport.view.OAImportView', {
    extend: 'Ext.panel.Panel',
    xtype: 'oaimportView',
    itemId: 'oaimportViewId',
    layout: 'border',
    items: [{
        region: 'north',
        height: 50,
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        items: [
            //     {
            //     width: 150,
            //     xtype: 'button',
            //     text: '导入OA机构',
            //     itemId: 'importOrgan',
            //     margin: '10'
            // },{
            //     width: 150,
            //     xtype: 'button',
            //     text: '导入OA用户',
            //     itemId: 'importUser',
            //     margin: '10'
            // },{
            //     width: 150,
            //     xtype: 'button',
            //     text: '导入OA历史条目',
            //     itemId: 'importEntries',
            //     margin: '10'
            // },
            {
                width: 150,
                xtype: 'button',
                text: '批量授权-角色',
                itemId: 'roleAuthorize',
                margin: '10'
            },{
                width: 150,
                xtype: 'button',
                text: '批量授权-工作流',
                itemId: 'nodeAuthorize',
                margin: '10'
            }]
    }]
});