/**
* Created by RonJiang on 2017/10/24 0024.
*/
Ext.define('MetadataSearch.view.SimpleSearchGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'simpleSearchGridView',
    title: '当前位置：元数据检索',
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
        itemId:'lookServiceMetadata',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看追溯元数据'
    }, '-', {
        itemId:'viewBookmarks',
        xtype: 'button',
        iconCls:'fa fa-heart',
        text: '查看收藏',
        hidden:true
    }, '-', {
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印'
    }, '-',
        {
            itemId: 'stborrowmenu',
            text: '实体查档单',
            iconCls: '',
            hidden:true,
            menu: [{
                itemId: 'stAdd',
                text: '添加实体查档单',
                iconCls: 'fa fa-plus-circle',
                menu: null
            }, {
                    itemId: 'lookAdd',
                    text: '处理实体查档单',
                    iconCls: 'fa fa-indent',
                    menu: null
                }]
        }, '-',
        // {
        //     itemId: 'electronborrowmenu',
        //     text: '电子查档单',
        //     iconCls: '',
        //    // hidden:true,
        //     menu: [{
        //         itemId: 'electronAdd'
        //         , text: '添加电子查档',
        //         iconCls: 'fa fa-plus-circle',
        //         menu: null
        //     },
        //         {
        //             itemId: 'dealElectronAdd',
        //             text: '处理电子查档',
        //             iconCls: 'fa fa-indent',
        //             menu: null
        //         }]
        // },'-',
        {
            itemId: 'electronprint',
            text: '打印申请单',
            iconCls: '',
            hidden:true,
            menu: [{
                itemId: 'addApplyPrint',
                text: '添加打印申请单',
                iconCls: 'fa fa-plus-circle',
                menu: null
            },
                {
                    itemId: 'dealApplyPrint',
                    text: '处理打印申请单',
                    iconCls: 'fa fa-indent',
                    menu: null
                }]
        },'-', {
            itemId: 'submitborrow',
            text: '提交查档单',
            iconCls: '',
            hidden:true,
            menu: [{
                itemId: 'submiteleborrow',
                text: '提交电子查档单',
                iconCls: 'fa fa-plus-circle',
                menu: null
            }, {
                    itemId: 'submitstborrow',
                    text: '提交实体查档单',
                    iconCls: 'fa fa-plus-circle',
                    menu: null
            }]
        }
        ],
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
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true}
        // {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});