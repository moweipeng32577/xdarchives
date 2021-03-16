/**
 * Created by Administrator on 2019/10/31.
 */

Ext.define('Management.view.LookBackCaptureDocEntryView',{
    extend:'Comps.view.EntryGridView',
    xtype:'lookBackCaptureDocEntryView',
    dataUrl:'/management/getBackCaptureEntrys',
    hasCloseButton:false,
    tbar:[{
        text:'返回',
        itemId:'lookBackEntryId'
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
