/**
 * Created by Leo on 2020/9/23 0023.
 */
Ext.define('TransforAuditDeal.view.AuditVolumeGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'auditVolumeGrid',
    dataUrl:'/audit/entries',
    tbar: [{
        text: '查看',
        iconCls: 'fa fa-eye',
        itemId: 'look'
    }, '-', {
        text: '修改',
        iconCls: 'fa fa-pencil-square-o',
        xtype: 'button',
        itemId: 'modify'
    }, '-', {
        text: '批量修改',
        iconCls: 'fa fa-pencil',
        xtype: 'button',
        itemId: 'batchModify'
    }, '-', {
        text: '批量替换',
        iconCls: 'fa fa-pencil-square-o',
        xtype: 'button',
        itemId: 'batchReplace'
    }, '-', {
        text: '批量增加',
        iconCls: 'fa fa-pencil-square',
        xtype: 'button',
        itemId: 'batchAdd'
    }, '-', {
        text: '数据查重',
        itemId: 'dataDuplicate',
        hidden:true
    }, '-', {
        text: '档号对齐',
        itemId: 'numberAlignment',
        hidden:true
    }],
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
    },
    hasSearchBar:true,
    hasCloseButton:false,
    hasSelectAllBox:true
});
