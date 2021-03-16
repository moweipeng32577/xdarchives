/**
 * Created by Administrator on 2020/7/8.
 */


Ext.define('SupervisionGuidance.store.SelectYearStore',{
    extend:'Ext.data.Store',
    fields: ['selectyear'],
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getSelectYear',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
