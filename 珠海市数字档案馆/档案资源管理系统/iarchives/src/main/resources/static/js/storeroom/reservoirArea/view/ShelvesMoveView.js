/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('ReservoirArea.view.ShelvesMoveView', {
    //extend: 'Comps.view.EntryGridView',
    extend:'Ext.panel.Panel',
    xtype: 'shelvesMoveView',
    layout:'border',
    items:[{
        region:'center',
        flex:7,
        itemId:'detailgrid',
        xtype:'basicgrid',
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        hasSearchBar:false,
        store:'DetailGridStore',
        columns: [
            {text: '列', dataIndex: 'coldisplay', flex: 1, menuDisabled: true},
            {text: '节', dataIndex: 'sectiondisplay', flex: 1, menuDisabled: true},
            {text: '层', dataIndex: 'layerdisplay', flex: 1, menuDisabled: true},
            {text: '面', dataIndex: 'sidedisplay', flex: 1, menuDisabled: true}
        ]

    }]
});