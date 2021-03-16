/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('BatchModify.view.BatchModifyResultGridView',{
    extend: 'Comps.view.EntryGridView',
    xtype:'batchModifyResultGridView',
    title: '当前位置：',
    tbar: [{
        itemId:'batchModifyBackId',
        xtype: 'button',
        iconCls:'fa fa-arrow-left',
        text: '返回'
    },'-',{
        itemId:'batchModifyShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    },'-',{
        itemId:'batchModifyModifyId',
        xtype: 'button',
        iconCls:'fa fa-pencil',
        text: '批量修改'
    },'-',{
        itemId:'batchModifyReplaceId',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '批量替换'
    },'-',{
        itemId:'batchModifyAddId',
        xtype: 'button',
        iconCls:'fa fa-pencil-square',
        text: '批量增加'
    }],
    dataUrl:'/classifySearch/findBySearch',
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }
});