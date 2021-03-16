/**
 * Created by tanly on 2017/12/6 0006.
 */

Ext.define('Outware.view.OutWareDetailView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'outWareDetailView',
    dataUrl: '/management/findOutWareEntry/',
    tbar: [{
        text: '查看',
        itemId: 'lookDetail'
    }, '-', {
        text: '导出',
        itemId: 'export'
    }/*, '-', {
        text: '打印',
        itemId: 'print'
    }*/],
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
    hasSelectAllBox: true
});