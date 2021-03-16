/**
 * Created by Leo on 2021/2/5 0005.
 */
Ext.define('Acquisition.view.transfor.AcquisitionPreviewTransEntryGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'acquisitionPreviewTransEntryGridView',
    dataUrl:'/acquisition/docPreviewEntry',
    hasCloseButton:false,
    hasSelectAllBox:true,
    tbar:[{
        text:'移交',
        itemId:'transfor'
    },'-',{
        text:'移除',
        itemId:'delete'
    },'-',{
        text:'查看',
        itemId:'look'
    },'-',{
        text:'返回',
        itemId:'back'
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