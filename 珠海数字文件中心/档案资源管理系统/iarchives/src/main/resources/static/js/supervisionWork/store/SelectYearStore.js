/**
 * Created by Administrator on 2020/7/8.
 */


Ext.define('SupervisionWork.store.SelectYearStore',{
    extend:'Ext.data.Store',
    fields: ['selectyear'],
    proxy: {
        type: 'ajax',
        url: '/supervisionWork/getSelectYear',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
