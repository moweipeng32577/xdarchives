/**
 * Created by tanly on 2018/2/5 0005.
 */
Ext.define('DuplicateChecking.view.DuplicateCheckingGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'duplicateCheckingGridView',
    itemId: 'duplicateCheckingGridViewId',
    dataUrl: '/duplicateChecking/findBySearch',
    templateUrl: '/template/changeGrid',
    tbar: functionButton,
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