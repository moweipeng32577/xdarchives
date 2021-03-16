/**
 * Created by Administrator on 2019/3/15.
 */



Ext.define('CompilationAcquisition.view.ManagementMissPageDetailView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'managementMissPageDetailView',
    header:false,
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    store: 'ManagementMissPageDetailStore',
    tbar:{
        items:[
            {
                text:'导出',
                iconCls:'fa fa-download',
                itemId:'export'
            }
        ]
    },
    columns: [
        {text: '档号', dataIndex: 'archivecode', flex: 2},//档号 实体属性：archivecode
        {text: '页数', dataIndex: 'page', flex: 2, menuDisabled: true},//文件日期 实体属性：filedate
        {text: '原文数量', dataIndex: 'elenumber', flex: 2, menuDisabled: true},//题名 实体属性：title
        {text: '漏页结果', dataIndex: 'result', flex: 2, menuDisabled: true},//责任者 实体属性：responsible
        {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'operate',
            text: '查看明细',
            align: 'center',
            itemId:'mxBtn',
            renderer: function (value, metaData, record) {
                var id = metaData.record.id;
                metaData.tdAttr = 'data-qtip="查看当前明细"';
                Ext.defer(function () {
                    Ext.widget('button', {
                        renderTo: id,
                        height: 25,
                        width: 80,
                        iconCls:'fa fa-files-o',
                        text: '明细',
                        // handler: function () {
                        //
                        // }
                    });
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }
    ]
});
