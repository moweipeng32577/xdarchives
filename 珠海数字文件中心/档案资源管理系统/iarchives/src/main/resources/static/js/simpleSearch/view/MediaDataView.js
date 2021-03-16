/**
 * Created by Leo on 2019/11/19 0019.
 */
Ext.define('SimpleSearch.view.MediaDataView', {
    extend: 'Comps.view.BasicDataView',
    xtype: 'mediadtView',
    hasPageBar:true,            //分页栏
    hasSearchBar:false,          //搜索栏
    hasCloseButton:false,        //关闭按钮
    hasCancelButton:false,
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
    }
    // , '-', {
    //     itemId:'setBookmarks',
    //     xtype: 'button',
    //     iconCls:'fa fa-star',
    //     text: '收藏'
    // }, '-', {
    //     itemId:'viewBookmarks',
    //     xtype: 'button',
    //     iconCls:'fa fa-heart',
    //     text: '查看收藏'
    // }
    , '-', {
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印'
    }, '-',
        // {
        //     itemId: 'stborrowmenu',
        //     text: '实体借阅',
        //     iconCls: '',
        //     menu: [{
        //         itemId: 'stAdd',
        //         text: '添加实体',
        //         iconCls: 'fa fa-plus-circle',
        //         menu: null
        //     },
        //         {
        //             itemId: 'lookAdd',
        //             text: '处理实体',
        //             iconCls: 'fa fa-eye',
        //             menu: null
        //         }]
        // }, '-',
        {
            itemId: 'electronborrowmenu',
            text: '查档申请单',
            iconCls: '',
            menu: [{
                itemId: 'electronAdd'
                , text: '添加查档',
                iconCls: 'fa fa-plus-circle',
                menu: null
            }, {
                itemId: 'dealElectronAdd',
                text: '处理查档',
                iconCls: 'fa fa-indent',
                menu: null
            }]
        }, '-' ,
        {
            itemId: 'stAddDoc',
            iconCls: 'fa fa-bars',
            text: '提交申请单',
            // menu: [{
            //     itemId: 'stAddDoc',
            //     iconCls:'fa fa-check-square',
            //     text: '提交实体借阅',
            //     menu: null
            // }, {
            //     itemId: 'electronAddDoc',
            //     iconCls:'fa fa-check-square',
            //     text: '提交电子借阅',
            //     menu: null
            // }]
        }, '-' , {
            itemId: 'gridShowId',
            iconCls: 'fa fa-bars',
            text: '列表显示'
        }],
    datastore: 'MediaDtStore'
});
