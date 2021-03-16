/**
 * Created by tanly on 2017/12/6 0006.
 */

Ext.define('ReservoirArea.view.CellDetailView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'cellDetailView',
    dataUrl: '/management/getCellEntry/',
/*    tbar: [{
        text: '查看',
        itemId: 'lookDetail'
    }, '-', {
        text: '导出',
        itemId: 'export'
    }/!*, '-', {
        text: '打印',
        itemId: 'print'
    }*!/],*/
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            autoLoad:true,
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