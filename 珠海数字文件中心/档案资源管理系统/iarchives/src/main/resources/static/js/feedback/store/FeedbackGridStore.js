/**
 * Created by RonJiang on 2018/04/17.
 */
Ext.define('Feedback.store.FeedbackGridStore',{
    extend:'Ext.data.Store',
    model:'Feedback.model.FeedbackGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/feedback/getFeedback',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
