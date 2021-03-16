/**
 * Created by Leo on 2020/7/3 0003.
 */
Ext.define('ConsultStandingBook.store.ConsultStandingBookStore',{
    extend:'Ext.data.Store',
    model:'ConsultStandingBook.model.ConsultStandingBookModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/consultStatistics/findConsultStatistics',
        extraParams:{startdate:'',enddata:''},
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
