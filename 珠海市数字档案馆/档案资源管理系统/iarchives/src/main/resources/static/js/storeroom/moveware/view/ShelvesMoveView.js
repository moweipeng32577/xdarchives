/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Moveware.view.ShelvesMoveView', {
    //extend: 'Comps.view.EntryGridView',
    extend:'Ext.panel.Panel',
    xtype: 'shelvesMoveView',
    layout:'border',
    items:[{
        region:'west',
        flex:3,
        xtype:'basicgrid',
        itemId:'shelvesgrid',
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        store:'ShelvesGridStore',
        hasSearchBar:false,
        margin:'10 5 10 10',
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
        itemId:'detailgrid',
        xtype:'basicgrid',
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        hasSearchBar:false,
        tbar:['选中的单元格: ', {text:'',itemId:'changeCell'},/* '移动的单元格: ', {text:'',itemId:'moveCell'},'放置的单元格: ', {text:'',itemId:'putCell'},*/{text:'移动',itemId:'move'},{text:'放置',itemId:'add'},{text:'查看',itemId:'look'},'<p style="color=red">单元格移动：先选择一个单元格点击移动，然后再选择另一个单元格进行放置！ </p>' ],
        store:'DetailGridStore',
        margin:'10 10 10 5',
        columns: [
            {text: '列', dataIndex: 'coldisplay', flex: 1, menuDisabled: true},
            {text: '节', dataIndex: 'sectiondisplay', flex: 1, menuDisabled: true},
            {text: '层', dataIndex: 'layerdisplay', flex: 1, menuDisabled: true},
            {text: '面', dataIndex: 'sidedisplay', flex: 1, menuDisabled: true}
        ]

    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "sourceShid",
        id:'sourceShid',
        name:'sourceShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "targetShid",
        id:'targetShid',
        name:'targetShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "tempShid",
        id:'tempShid',
        name:'tempShid',
        hidden: true
    },{
        columnWidth:.1,
        xtype:'textfield',
        itemId: "zoneId",
        id:'zoneId',
        name:'zoneId',
        hidden: true
    }]
});