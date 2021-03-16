/**
 * Created by Administrator on 2020/10/13.
 */


Ext.define('BusinessYearlyCheck.view.BusinessNewYearlyCheckReportView',{
    extend:'Comps.view.BasicGridView',
    xtype:'businessNewYearlyCheckReportView',
    searchstore: [
        {item: "selectyear", name: "年度"},
        {item: "title", name: "题名"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'新增',
            iconCls:'fa fa-plus-circle',
            itemId:'addId'
        },'-',{
            text:'修改',
            iconCls:'fa fa-pencil-square-o',
            itemId:'editId'
        },'-',{
            text:'删除',
            iconCls:'fa fa-trash-o',
            itemId:'deleteId'
        },'-',{
            text:'查看',
            iconCls:'fa fa-eye',
            itemId:'lookId'
        },'-',{
            text:'提交',
            iconCls:'fa fa-share-square',
            itemId:'submitId'
        }]
    },
    columns:[
        {text: '年度', dataIndex: 'selectyear',flex: 2, menuDisabled: true},
        {text: '题名', dataIndex: 'title', flex: 4, menuDisabled: true},
        {text: '状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'operate',
            text: '操作',
            align: 'center',
            itemId:'downloadBtn',
            renderer: function (value, metaData, record) {
                var id = "new"+metaData.record.id;
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
                var id = 'newprint' + metaData.record.id;
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
    store: 'BusinessNewYearlyCheckReportStore'
});
