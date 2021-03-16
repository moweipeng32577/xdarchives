/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('Inware.view.ReturnWareView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'returnWareView',
    dataUrl: '/management/returnOutwares',//查看出库记录
    tbar: [{
        text: '入库',//顺便修改该档案的库存状态
        itemId: 'add'
    },'单条档案入库，支持自动打开密集架！'],
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
    hasSelectAllBox:true,
    hasCloseButton:false
});