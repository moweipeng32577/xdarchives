/**
 * Created by Administrator on 2019/10/31.
 */


Ext.define('Management.model.LookBackCaptureDocGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'backreason', type: 'string'},
        {name: 'backer', type: 'string'},
        {name: 'backorgan', type: 'string'},
        {name: 'backtime', type: 'string'},
        {name: 'backcount', type: 'string'}
    ]
});
