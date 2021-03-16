/**
 * Created by Administrator on 2019/3/2.
 */


Ext.define('ArchivesCallout.view.ArchivesCalloutImportView', {
    extend: 'Ext.panel.Panel',
    xtype:'archivesCalloutImportView',
    header:false,
    layout:'border',
    items:[{
        region:'north',
        height:0
    },{
        region:'west',
        width:0,
        margin:'0 5 0 0',
        //title:'系统',
        items:[{
            xtype:'treelist',
            store:{
                root: {
                    expanded:true,
                    children: [/*{
                     text: '超越2000',
                     iconCls: null,
                     leaf: true
                     },{
                     text: 'Darms',
                     iconCls: null,
                     leaf: true
                     }*/]
                }
            }
        }]
    },{
        region:'center',
        itemId:'workspace',
        layout:'border',
        items:[{
            xtype:'form',
            region:'north',
            layout:'hbox',
            items:[{
                xtype: 'fieldset',
                title: '源数据文件',
                margin:'0 5 5 5',
                flex: 1,
                layout:'fit',
                items:[{
                    xtype: 'filefield',
                    itemId:'filefieldImport',
                    clearOnSubmit:false,
                    name:'source',
                    buttonText:'打开',
                    allowBlank:false,
                    hideLabel: true
                }]
            }, {
                xtype: 'fieldset',
                title: '目的数据节点',
                margin: '0 5 5 0',
                layout: 'fit',
                flex: 1,
                items: [{
                    xtype: 'TreeComboboxView',
                    fieldLabel: '档案分类',
                    editable: false,
                    url: '/nodesetting/getSzhWCLNodeByParentId',
                    extraParams: {pcid: ''},
                    allowBlank: false,
                    name: 'nodename',
                    itemId: 'dismantleNode'
                }]
            }],
            buttons: [
                { text: '开始导入',itemId:'import'},
                { text: '关闭',itemId:'close',handler:function (btn) {
                    btn.up('window').close();
                }}
            ]
        }]
    }]
});
