/**
 * Created by Administrator on 2019/3/15.
 */


Ext.define('Acquisition.store.AcquisitionMissPageDetailStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.AcquisitionMissPageDetailModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/acquisition/getMissPageCheck',
        //method: 'post',
        extraParams:{
            ids:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        },
        actionMethods:{read:'POST'}
    }
});
