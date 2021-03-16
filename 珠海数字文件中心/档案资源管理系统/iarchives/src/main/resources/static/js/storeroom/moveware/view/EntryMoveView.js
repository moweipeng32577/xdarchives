/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Moveware.view.EntryMoveView', {
    //extend: 'Comps.view.EntryGridView',
    extend:'Ext.panel.Panel',
    xtype: 'entryMoveView',
    //dataUrl: '/inware/inwares',


    layout:'fit',
    modal:true,
    hasSearchBar:false,
    items:[{
        xtype:'form',
        layout:'column',
        bodyPadding:10,
        defaults:{
            xtype:'textfield',
            labelAlign:'right',
            labelWidth:100,
            margin:'5 5 0 5'
        },
        items:[{
            columnWidth:1,

            layout : "column",
            xtype:'fieldset',
            style:'background:#fff;padding-top:0px',
            title: '电子档案关联',
            autoHeight:true,
            labelWidth:60,
            labelAlign:'right',
            animCollapse :true,

            autoScroll: true,
            renderTo: document.body,
            items:[
                {
                    columnWidth:.18,
                    xtype: "button",
                    itemId: "dzbtn",
                    text: "选择档案"
                },{
                    //columnWidth:1,
                    xtype: 'displayfield',//空白间隔
                    id: 'picker7',
                    width: 5
                    //height: .1
                },{
                    columnWidth:1,
                    xtype:'textarea',
                    itemId: "idsName",
                    //fieldLabel: '电子档案',
                    //name:'idsName',
                    editable:false
                }
            ]

        },{
            columnWidth:1,
            xtype:'textfield',
            itemId: "ids",
            fieldLabel: '电子档案编号',
            name:'ids',
            //name:'wareuser',
            hidden: true
        }]
    }],
    buttons:[{text:'保存',itemId:'save'}/*,{text:'取消',itemId:'cancel'}*/]
});