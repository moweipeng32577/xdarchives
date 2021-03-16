/**
 * Created by Administrator on 2020/10/13.
 */


Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckReportView',{
    extend:'Comps.view.BasicGridView',
    xtype:'businessYearlyCheckReportView',
    searchstore: [
        {item: "selectyear", name: "年度"},
        {item: "title", name: "题名"}
    ],
    columns:[
        {text: '年度', dataIndex: 'selectyear', flex: 2, menuDisabled: true},
        {text: '题名', dataIndex: 'title', flex: 4, menuDisabled: true},
        {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'operate',
            text: '操作',
            align: 'center',
            itemId:'downloadBtn',
            renderer: function (value, metaData, record) {
                var id = metaData.record.id;
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        height: 25,
                        width: 80,
                        iconCls:'fa fa-download',
                        text: '下载'
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }, {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'operate',
            text: '操作',
            align: 'center',
            itemId: 'printBtn',
            renderer: function (value, metaData, record) {
                var id = 'print' + metaData.record.id;
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        height: 25,
                        width: 80,
                        iconCls: 'fa fa-print',
                        text: '打印'
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }
    ],
    store: 'BusinessYearlyCheckReportStore'
});
