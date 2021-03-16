/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Appraisal.view.AppraisalBillEntryGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'appraisalBillEntryGridView',
    hasCloseButton:false,
    searchstore:[
        {item: 'title', name: '题名'},
        {item: 'archivecode', name: '档号'},
        {item: 'entryretention', name: '保管期限'},
        {item: 'filedate', name: '文件日期'}
    ],
    tbar:[{
        text:'返回',
        itemId:'back'
    }],
    store: 'BillEntryGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '责任者', dataIndex: 'responsible', flex: 2, menuDisabled: true},
        {text: '文件日期', dataIndex: 'filedate', flex: 2, menuDisabled: true},
        {text: '保管期限', dataIndex: 'entryretention', flex: 2, menuDisabled: true},
        {text: '已保管时间', dataIndex: 'keepdate', flex: 2, menuDisabled: true},
        {text: '销毁时间', dataIndex: 'destroydate', flex: 2, menuDisabled: true}
    ]
});