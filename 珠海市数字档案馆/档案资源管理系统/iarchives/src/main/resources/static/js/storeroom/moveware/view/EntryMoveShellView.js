/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Moveware.view.EntryMoveShellView', {
    //extend: 'Comps.view.EntryGridView',
    extend:'Ext.panel.Panel',
    xtype: 'entryMoveShellView',
    layout:'border',
    items:[{
        region:'north',
        columnWidth:1,
        layout : "column",
        xtype:'fieldset',
        style:'background:#fff;padding-top:0px',
        title: '电子档案关联',
        autoHeight:true,
        labelWidth:60,
        labelAlign:'right',
        animCollapse :true,
        margin:'10 10 5 10',
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
                id: 'picker17',
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
        region:'west',
        flex:3,
        xtype:'basicgrid',
        itemId:'shelvesgridTwo',
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        store:'ShelvesGridStore',
        hasSearchBar:false,
        margin:'5 5 10 10',
        columns: [
            {text: '城区', dataIndex: 'citydisplay', flex: 1, menuDisabled: true},
            {text: '单位', dataIndex: 'unitdisplay', flex: 2, menuDisabled: true},
            {text: '楼层', dataIndex: 'floordisplay', flex: 1, menuDisabled: true},
            {text: '库房', dataIndex: 'roomdisplay', flex: 1, menuDisabled: true},
            {text: '密集架区', dataIndex: 'zonedisplay', flex: 2, menuDisabled: true}
        ]
    },{
        region:'center',
        flex:7,
        itemId:'detailgridTwo',
        xtype:'basicgrid',
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        hasSearchBar:false,
        tbar:['已选中单元格: ', {text:'',itemId:'change'},{text:'放置',itemId:'addTwo'},{text:'查看',itemId:'look'}],
        store:'DetailGridStore',
        margin:'5 10 10 5',
        columns: [
            {text: '列', dataIndex: 'coldisplay', flex: 1, menuDisabled: true},
            {text: '节', dataIndex: 'sectiondisplay', flex: 1, menuDisabled: true},
            {text: '层', dataIndex: 'layerdisplay', flex: 1, menuDisabled: true},
            {text: '面', dataIndex: 'sidedisplay', flex: 1, menuDisabled: true}
        ]

    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "sourceShidTwo",
        id:'sourceShidTwo',
        name:'sourceShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "targetShidTwo",
        id:'targetShidTwo',
        name:'targetShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "tempShidTwo",
        id:'tempShidTwo',
        name:'tempShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "zoneIdTwo",
        id:'zoneIdTwo',
        name:'zoneId',
        hidden: true
    },{
        columnWidth:1,
        xtype:'textfield',
        itemId: "ids",
        fieldLabel: '电子档案编号',
        name:'ids',
        //name:'wareuser',
        hidden: true
    }]
});