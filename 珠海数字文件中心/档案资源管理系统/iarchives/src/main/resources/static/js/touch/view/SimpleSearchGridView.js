/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('Touch.view.SimpleSearchGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'simpleSearchGridView',
    title: '当前位置：简单检索',
    itemId:'simpleSearchGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    tbar: [{
        itemId:'simpleSearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
        }],
    viewConfig:{
      getRowClass:function () {
          return 'x-grid-row custom-grid-row';
      }
    },
    store: 'SimpleSearchGridStore',
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
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            sortable:true,
            width:60,
            align:'center',
            items:['@file']
        },
        {text: '档号', dataIndex: 'archivecode', flex: 2},//档号 实体属性：archivecode
        {text: '题名', dataIndex: 'title', flex: 3, menuDisabled: true},//题名 实体属性：title
        {text: '文件日期', dataIndex: 'filedate', flex: 1, menuDisabled: true},//文件日期 实体属性：filedate
        {text: '责任者', dataIndex: 'responsible', flex: 1, menuDisabled: true},//责任者 实体属性：responsible
        {text: '文件编号', dataIndex: 'filenumber', flex: 1, menuDisabled: true},//文件编号 实体属性：filenumber
        {text: '全宗号', dataIndex: 'funds', flex: 1, menuDisabled: true},//全宗号  实体属性:funds
        {text: '目录号', dataIndex: 'catalog', flex: 1, menuDisabled: true},//目录号  实体属性:catalog
        {text: '案卷号', dataIndex: 'filecode', flex: 1, menuDisabled: true},//案卷号  实体属性:filecode
        {text: '件号', dataIndex: 'recordcode', flex: 1, menuDisabled: true},//件号  实体属性:recordcode
        {text: '归档年度', dataIndex: 'filingyear', flex: 1, menuDisabled: true},//归档年度  实体属性:filingyear
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});