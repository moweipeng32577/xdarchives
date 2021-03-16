/**
 * Created by tanly on 2017/12/6 0006.
 */

Ext.define('Inware.view.HistoryView',{
    extend:'Ext.panel.Panel',
    xtype:'historyView',
    layout : 'fit',
    items:[{
        itemId:'pairgrid',
        layout:{
            type:'vbox',
            pack: 'start',
            align: 'stretch'
        },
        items:[{
            flex: 5,
            xtype: 'basicgrid',
            itemId: 'inwaregrid',
            store: 'InwareStore',
            hasSearchBar: false,
            selType: 'rowmodel',//默认checkboxmodel 是选择框
            //margin:'15 15 15 15',
            columns: [
                {text: '入库时间', dataIndex: 'waretime', flex: 2, menuDisabled: true},
                {text: '入库类型', dataIndex: 'waretype', flex: 1, menuDisabled: true},
                {text: '入库人', dataIndex: 'wareuser', flex: 1, menuDisabled: true},
                // {text: '归还人', dataIndex: 'returnware', flex: 1, menuDisabled: true},
                {text: '备注', dataIndex: 'description', flex: 3, menuDisabled: true}
            ],
            // searchstore:[
            // {item: "waretime", name: "入库时间"},
            // {item: "waretype", name: "入库类型"},
            // {item: "wareuser", name: "入库人"}
            // ]
        },{
            flex:5,
            title:'详细条目信息',
            xtype:'inWareDetailView'
        }
        ]
    }]
});