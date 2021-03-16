/**
 * Created by RonJiang on 2018/1/30 0030.
 */
Ext.define('Inware.view.ImportResultPreviewGrid',{
    extend: 'Comps.view.EntryGridView',
    xtype:'importResultPreviewGrid',
    hasCheckColumn:false,
    hasCancelButton:false,
    hasCloseButton:false,
    hasSearchBar:false,
    templateUrl: '/template/changeGrid?nodeid=publicNode',
    tbar: [{
        itemId:'setCodeBtn',
        xtype: 'button',
        text: '入库匹配字段设置'
    },{
        itemId:'checkImportBtn',
        xtype: 'button',
        text: '执行入库信息匹配'
    },'-',{
        itemId:'resultShowBtn',
        xtype: 'button',
        text: '匹配结果查询',
        menu: [{
            text:'可以进行入库',
            itemId:'successId',
            menu: null
        },{
            text:'没匹配到相关条目',
            itemId:'notFundID',
            menu: null
        },{
            text:'匹配到多条条目',
            itemId:'moreFundID',
            menu: null
        },{
            text:'存储位置不够详细',
            itemId:'notDetailID',
            menu: null
        },{
            text:'放入密集架空间不足',
            itemId:'noSpaceID',
            menu: null
        },{
            text:'已入库',
            itemId:'inStorageID',
            menu: null
        },{
            text:'存储位置没有匹配到',
            itemId:'noZoneID',
            menu: null
        },{
            text:'存储位置信息为空',
            itemId:'nullID',
            menu: null
        },{
            text:'显示所有',
            itemId:'allID',
            menu: null
        }]
    },'-',{
        itemId:'doImportBtn',
        xtype: 'button',
        text: '执行入库'
    },'-',{
        itemId:'exportBtn',
        xtype: 'button',
        text: '入库失败信息导出'
    },'-',{
        itemId:'backBtn',
        xtype: 'button',
        text: '返回'
    }, '-', {//当前匹配字段
        xtype: 'label',
        text: ' ',
        itemId: 'txtLabelId',
        style: {
            color: 'blue',
            'font-size': '17px',
            'font-weight': 'bold'
        }
    }, '-', {//匹配结果
        xtype: 'label',
        text: '',
        itemId: 'txtResultLabelId',
        style: {
            color: 'blue',
            'font-size': '17px',
            'font-weight': 'bold'
        }
    }],
    dataUrl:'/batchModify/getKfResultPreview'
});