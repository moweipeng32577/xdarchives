/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Datareceive.view.ImportView', {
    extend: 'Ext.panel.Panel',
    xtype:'import',
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
                    children: []
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
            height:95,
            layout:'hbox',
            items:[{
                xtype: 'fieldset',
                title: '目的数据节点',
                margin:'0 5 5 0',
                layout:'fit',
                flex: 1,
                items:[{
                    xtype: 'dataNodeComboView',
                    fieldLabel: '档案分类',
                    editable: true,
                    url: '/import/datanodes',
                    extraParams:{pid:''},
                    allowBlank: false
                }]
            },{
                xtype: 'fieldset',
                title: '源数据文件',
                margin:'0 5 5 5',
                flex: 1,
                layout:'fit',
                hidden: true,
                items:[{
                    xtype: 'textfield',
                    name:'source'
                }]
            },{
                xtype:'hidden',
                name:'target'
            }]
        },{
            region:'center',
            border:false,
            layout:'border',
            items:[{
                title:'字段设置',
                region:'west',
                width:400,
                margin:'0 5 0 0',
                xtype:'grid',
                itemId:'fieldgrid',
                store:'ImportGridStore',
                columns:[
                    {xtype: 'rownumberer', align: 'center', width:40},
                    {text: '源字段',dataIndex:'source',flex:1},
                    {text: '目的字段',dataIndex:'target',flex:1,editor:{
                        xtype: 'combo',
                        typeAhead: true,
                        triggerAction: 'all',
                        queryMode: 'local',
                        forceSelection:true,
                        store: 'TemplateStore',
                        displayField:'fieldname',
                        valueField:'fieldname'
                    }}
                ],
                plugins: {
                    ptype: 'cellediting',
                    clicksToEdit: 1
                }
            },{
                title:'导入预览(仅显示前10条数据)',
                region:'center',
                xtype:'grid',
                itemId:'previewgrid',
                tbar:[{
                    text:'开始导入',
                    itemId:'impBtn'
                },{
                    text:'返回',
                    itemId:'back'
                },{
                    columnWidth: .09,
                    xtype: 'checkboxfield',
                    boxLabel: 'BSxml',
                    style: "margin-left:6px;margin-right:10px",
                    itemId: 'taitanXml',
                    hidden: true
                },{
                    columnWidth: .09,
                    xtype: 'checkboxfield',
                    boxLabel: '社保中心xml',
                    style: "margin-left:6px;margin-right:10px",
                    itemId: 'socialSecurityXml',
                    hidden: true
                }]
            }]
        }],
        listeners:{
           /* close: function (win) {
                console.log("123")
                /!*Ext.Ajax.request({
                    url: '/import/deletUploadFile',
                });*!/
            }*/
           'beforeDestroy':function(tab){
                console.log("123")
            }
        }
    }]
});
