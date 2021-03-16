/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.view.DuplicateCheckingGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'duplicateCheckingGridView',
    layout:'card',
    activeItem:0,
    itemId: 'duplicateCheckingGridViewId',
    dataUrl: '/duplicateChecking/auditFindBySearch',
    templateUrl: '/template/changeGrid',
    tbar: [{
        itemId:'look',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    },'-',{
        itemId:'edit',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    }
        // ,'-',{
        //     itemId:'delete',
        //     xtype: 'button',
        //     iconCls:'fa fa-trash-o',
        //     text: '删除'
        // }
    ],
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
    hasCloseButton:false
});
