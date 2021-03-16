/**
 * Created by RonJiang on 2018/1/30 0030.
 */
Ext.define('BatchModify.view.BatchModifyResultPreviewGrid',{
    extend: 'Comps.view.EntryGridView',
    xtype:'batchModifyResultPreviewGrid',
    hasCheckColumn:false,
    hasCancelButton:false,
    hasCloseButton:false,
    templateUrl: '/template/changeGrid',
    tbar: [{
        itemId:'batchUpdateBtn',
        xtype: 'button',
        text: '执行批量更新'
    },'-',{
        itemId:'backBtn',
        xtype: 'button',
        text: '返回'
    }],
    dataUrl:'/batchModify/getResultPreview',
    searchstore:{
        proxy: {
            type: 'ajax',
            // url:'/template/excludedQueryName',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            actionMethods:{read:'POST'},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }
});