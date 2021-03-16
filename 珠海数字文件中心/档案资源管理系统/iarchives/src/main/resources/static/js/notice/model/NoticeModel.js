Ext.define('Notice.model.NoticeModel',{
    extend:'Ext.data.Model',
    fields:[
        // {name:'userID',type:'string'},
        {name:'title',type:'string'},
        {name:'organ',type:'string'},
        {name:'content',type:'string'},
        // {name:'endtime',type:'string'},
        {name:'publishtime',type:'string'},
        {name:'publishstate',type:'string'},
        {name:'stick',type:'string'}
    ]
});