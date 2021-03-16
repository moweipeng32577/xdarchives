/**
 * Created by Administrator on 2019/6/17.
 */
Ext.define('Accept.model.AcceptdocFinishGridModel',{
    extend:'Ext.data.Model',
    fields:[
        {name:'id',type:'string',mapping:'acceptdocid'},
        {name:'submitter',type:'string'},
        {name:'accepter',type:'string'},
        {name:'organid',type:'string'},
        {name:'organ',type:'string'},
        {name:'accepdate',type:'string'},
        {name:'docstate',type:'string'}
    ]
});
