/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Import.view.ImportView', {
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
        /*tools:[{
            type:'help',
            callback:function(){
                var win = Ext.create('Ext.window.Window',{
                    title:'帮助',
                    maximizable:true,
                    width:960,
                    height:600,
                    layout:'fit',
                    items:[{
                        xtype:'image',
                        src:'../../img/help.png'
                    }]
                });
                win.show();
            }
        }],*/
        items:[{
            xtype:'form',
            region:'north',
            height:95,
            layout:'hbox',
            items:[{
                xtype: 'fieldset',
                title: '源数据文件',
                margin:'0 5 5 5',
                flex: 1,
                layout:'fit',
                items:[{
                    xtype: 'filefield',
                    clearOnSubmit:false,
                    name:'source',
                    buttonText:'打开',
                    allowBlank:false,
                    hideLabel: true
                }]
            },{
                xtype: 'fieldset',
                title: '目的数据节点',
                margin:'0 5 5 0',
                layout:'fit',
                flex: 1,
                items:[{
                    xtype: 'dataNodeComboView',
                    fieldLabel: '档案分类',
                    editable: false,
                    url: '/import/datanodes',
                    extraParams:{pid:''},
                    allowBlank: false
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
                    columnWidth: .09,
                    xtype: 'checkboxfield',
                    boxLabel: '自动生成档号',
                    style: "margin-left:6px;margin-right:10px",
                    itemId: 'autoCreateArchivecode'
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
        },
    }]
});
