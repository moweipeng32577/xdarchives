/**
* Created by RonJiang on 2017/10/24 0024.
*/
Ext.define('PavilionSearch.view.PavilionSearchGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'pavilionSearchGridView',
    title: '当前位置：馆库查询',
    itemId:'pavilionSearchGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    tbar: [{
        itemId:'pavilionSearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
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
        itemId:'download',
        xtype: 'button',
        iconCls:'fa fa-download',
        text: '下载'
    }, '-', {
        itemId: 'downloadmenu',
        text: '下载申请',
        iconCls: '',
        menu: [{
            itemId: 'addApplyDownload',
            text: '添加下载申请',
            iconCls: 'fa fa-plus-circle',
            menu: null
        },
            {
                itemId: 'dealApplyDownload',
                text: '处理下载申请',
                iconCls: 'fa fa-indent',
                menu: null
            }]
    }, '-', {
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印'
    }, '-', {
        itemId: 'printmenu',
        text: '打印申请',
        iconCls: '',
        menu: [{
            itemId: 'addApplyPrint',
            text: '添加打印申请',
            iconCls: 'fa fa-plus-circle',
            menu: null
        },
            {
                itemId: 'dealApplyPrint',
                text: '处理打印申请',
                iconCls: 'fa fa-indent',
                menu: null
            }]
    }
    //     {
    //         itemId: 'stborrowmenu',
    //         text: '实体查档',
    //         iconCls: '',
    //         menu: [{
    //             itemId: 'stAdd',
    //             text: '添加实体',
    //             iconCls: 'fa fa-plus-circle',
    //             menu: null
    //         },
    //             {
    //                 itemId: 'lookAdd',
    //                 text: '处理实体',
    //                 iconCls: 'fa fa-eye',
    //                 menu: null
    //             }]
    //     }, '-',
    //     {
    //         itemId: 'electronborrowmenu',
    //         text: '电子查档',
    //         iconCls: '',
    //         menu: [{
    //             itemId: 'electronAdd'
    //             , text: '添加电子',
    //             iconCls: 'fa fa-plus-circle',
    //             menu: null
    //         },
    //             {
    //                 itemId: 'dealElectronAdd',
    //                 text: '处理电子',
    //                 iconCls: 'fa fa-indent',
    //                 menu: null
    //             }]
    //     },'-',

        ],
    store: 'PavilionSearchGridStore',
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
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});