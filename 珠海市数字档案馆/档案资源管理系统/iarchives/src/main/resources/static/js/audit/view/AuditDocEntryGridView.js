/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Audit.view.AuditDocEntryGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'auditDocEntryGridView',
    dataUrl:'/acquisition/docEntry',
    hasCloseButton:false,
    tbar:[{
        text:'返回',
        itemId:'back'
    },'-',{
        text:'查看',
        itemId:'look'
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
    }
});
