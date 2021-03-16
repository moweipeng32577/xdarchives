/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('DuplicateChecking.store.DuplicateCheckingGridStore',{
    extend:'Ext.data.Store',
    model:'DuplicateChecking.model.DuplicateCheckingGridModel',
    pageSize:XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/classifySearch/findBySearch',
        reader: {
            type: 'json',
            expanded: true,
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});