/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('ManageDirectory.view.ManageDirectoryGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'manageDirectoryGridView',
    dataUrl: '/manageDirectory/entries',
    title: '当前节点：',
    isOpenEle:false,
    tbar: {
        overflowHandler: 'scroller',
        items: [
            //     {
            //     text:'著录',
            //     iconCls:'fa fa-plus-circle',
            //     itemId:'save'
            // },'-',
            {
                text: '修改',
                iconCls: 'fa fa-pencil-square-o',
                itemId: 'modify'
            }, '-', {
                text: '删除',
                iconCls: 'fa fa-trash-o',
                itemId: 'del'
            }, '-', {
                text: '查看',
                iconCls: 'fa fa-eye',
                itemId: 'look'
            }, '-', {
                text: '批量处理',
                iconCls: '',
                itemId: 'batchDeal',
                menu: [
                    {
                        text: '批量修改',
                        iconCls: 'fa fa-pencil-square',
                        itemId: 'batchModify',
                        menu: null
                    },
                    {
                        text: '批量增加',
                        iconCls: 'fa fa-cart-plus',
                        itemId: 'batchAdd',
                        menu: null
                    },
                    {
                        text: '批量替换',
                        iconCls: 'fa fa-paypal',
                        itemId: 'batchRepace',
                        menu: null
                    }]
            }, '-', {
                text: '数据导出',
                iconCls: '',
                itemId: 'import',
                menu: [
                    {
                        text: '导出Excel',
                        iconCls: 'fa fa-level-down',
                        itemId: 'exportEx',
                        menu: null
                    },
                    // {
                    //     text:'导出Excel和原文',
                    //     iconCls:'fa fa-level-down',
                    //     itemId:'exportExAndfile',
                    //     menu: null
                    // },
                    {
                        text: '导出Xml',
                        iconCls: 'fa fa-level-down',
                        itemId: 'exportXml',
                        menu: null
                    },
                    // {
                    //     text:'导出Xml和原文',
                    //     iconCls:'fa fa-level-down',
                    //     itemId:'exportXmlAndfile',
                    //     menu: null
                    // },
                    // {
                    //     text:'数据导入',
                    //     iconCls:'fa fa-level-down',
                    //     itemId:'importData',
                    //     menu: null
                    // },{
                    //     text:'导出字段模板',
                    //     iconCls:'fa fa-level-down',
                    //     itemId:'exportFileCode',
                    //     menu: null
                    // }
                ]
            }, '-',{
                text: '退回目录传输',
                iconCls: 'fa fa-reply-all',
                itemId: 'backAccept'
            }]
    },
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            extraParams: {nodeid: 0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    hasSelectAllBox: true
});
