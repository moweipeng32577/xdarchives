/**
 * Created by Administrator on 2020/3/18.
 */


Ext.define('Feedback.store.AppraiseGridStore',{
    extend:'Ext.data.Store',
    model:'Feedback.model.AppraiseGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/feedback/getAllAppraise',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
