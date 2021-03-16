/**
 * Created by RonJiang on 2018/4/19 0019.
 */
Ext.define('Acquisition.view.transfor.AcquisitionTransdocEntryGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'acquisitionTransdocEntryGridView',
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