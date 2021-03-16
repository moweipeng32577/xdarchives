/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('SimpleSearchDirectory.view.SimpleSearchDirectoryGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'simpleSearchDirectoryGridView',
    title: '当前位置：简单检索',
    itemId:'simpleSearchGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    tbar: [{
        itemId:'simpleSearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        itemId:'simpleSearchExportId',
        xtype: 'button',
        iconCls:'fa fa-share-square-o',
        text: '导出Excel'
    }, '-', {
        itemId:'setBookmarks',
        xtype: 'button',
        iconCls:'fa fa-star',
        text: '收藏'
    }, '-', {
        itemId:'viewBookmarks',
        xtype: 'button',
        iconCls:'fa fa-heart',
        text: '查看收藏'
    }, '-', {
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印'
    }
    ],
    store: 'SimpleSearchDirectoryGridStore',
    listeners:{//排序监听
        headerclick: function (ct, c, e) {
            var store = c.up('grid').getStore();
            if(c.dataIndex!=='archivecode'&&store.totalCount > 100000&&c.dataIndex!=='nodefullname'){//档号是索引字段，可排序
                /*e.stopPropagation();*/
                XD.msg('查询数据量大于10万,只支持本页面排序');
                store.setRemoteSort(false);
            }else if(c.dataIndex==='nodefullname'){//所属节点不排序
                XD.msg('所属节点不支持排序');
                store.setRemoteSort(false);
            }else{
                store.setRemoteSort(true);
            }
        }
    },
    columns: [
        {text: '档号', dataIndex: 'archivecode', flex: 2},//档号 实体属性：archivecode
        {text: '题名', dataIndex: 'title', flex: 3, menuDisabled: true},//题名 实体属性：title
        {text: '文件日期', dataIndex: 'filedate', flex: 1, menuDisabled: true},//文件日期 实体属性：filedate
        {text: '责任者', dataIndex: 'responsible', flex: 1, menuDisabled: true},//责任者 实体属性：responsible
        {text: '文件编号', dataIndex: 'filenumber', flex: 1, menuDisabled: true},//文件编号 实体属性：filenumber
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});
