/**
 * Created by tanly on 2017/12/1 0001.
 */

var data = [
    ["调档出库","调档出库"],
    ["查档出库","查档出库"],
    ["转递出库","转递出库"]
];
var store = new Ext.data.SimpleStore({
    fields:["waretype","text"],
    data:data
});
Ext.define('Outware.view.TransferWareView', {
    //extend: 'Comps.view.EntryGridView',
    extend:'Ext.panel.Panel',
    xtype: 'transferWareView',
    //dataUrl: '/inware/inwares',
    /*tbar: [ {
        text: '生成出库记录',
        itemId: 'add'
    }],*/
   /* modal:true,
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',//根据提名或档号查找对应实体档案
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }*/
    layout:'fit',
    modal:true,
    hasSearchBar:false,
    items:[{
        xtype:'form',
        layout:'border',
        bodyPadding:10,
        defaults:{
            xtype:'textfield',
            labelAlign:'right',
            labelWidth:100,
            // margin:'5 5 0 5'
        },
        items:[{
            region: 'north',
            height: '48%',
            width: '84%',
            margin: '10px',
            /*fieldLabel: '电子档案关联',
             name:'description'*/
            layout: "column",
            xtype: 'panel',
            style: 'background:#fff;padding-top:0px',
            title: '电子档案关联',
            labelWidth: 60,
            // border:"1px",
            labelAlign: 'right',
            // animCollapse :true,
            autoScroll: true,
            renderTo: document.body,
            items: [
                {
                    margin: '10px',
                    columnWidth: .18,
                    xtype: "button",
                    itemId: "selectBorrowdoc",
                    text: "选择查档单据"
                }, {
                    margin: '10px 0px',
                    //columnWidth:1,
                    xtype: 'displayfield',//空白间隔
                    id: 'picker6',
                    width: 5
                    //height: .1
                }, {
                    margin: '10px 0px',
                    columnWidth: .18,
                    xtype: "button",
                    itemId: "dzbtn",
                    text: "选择档案"
                }, {
                    margin: '10px',
                    columnWidth: 1,
                    xtype: 'textarea',
                    height: "75%",
                    itemId: "idsName",
                    //fieldLabel: '电子档案',
                    //name:'idsName',
                    editable: false
                }
            ]

        }, {
            region:'center',
            height: '12%',
            /*fieldLabel: '电子档案关联',
             name:'description'*/
            margin: '10px',
            layout: "column",
            xtype: 'panel',
            style: 'background:#fff;padding-top:0px',
            title: '出库类型',
            labelWidth: 60,
            labelAlign: 'right',
            renderTo: document.body,
            items: [
                {
                    columnWidth: 1,
                    margin:"9px",
                    xtype: 'combo',
                    /* fieldLabel: '出库类型',
                     itemId: "waretype",
                     name:'waretype'*/

                    /*renderTo:Ext.getBody(),*/
                    store: store,
                    mode: "local",
                    displayField: 'text',
                    valueField: 'waretype',
                    triggerAction: 'all',
                    emptyText: '请选择',
                    name: 'waretype',
                    itemId: "waretype",
                    value: '调档出库'
                }
            ]
        }, {
            region:'south',
            height: '32%',
            /*fieldLabel: '电子档案关联',
             name:'description'*/
            margin: '10px',
            layout: "column",
            xtype: 'panel',
            style: 'background:#fff;padding-top:0px',
            title: '备注',
            labelWidth: 60,
            labelAlign: 'right',
            renderTo: document.body,
            items: [
                {
                    label: '备注',
                    columnWidth: 1,
                    xtype: 'textarea',
                    labelWidth:50,
                    fieldLabel: '备注',
                    name: 'description',
                    editable: true,
                    height: '80%',
                    margin: '10px'

                }
            ]
        }, {
            xtype: 'textfield',
            itemId: "ids",
            fieldLabel: '电子档案编号',
            name: 'ids',
            //name:'wareuser',
            hidden: true
        }]
    }],
    buttons:[{text:'保存',itemId:'save'}/*,{text:'取消',itemId:'cancel'}*/]
});