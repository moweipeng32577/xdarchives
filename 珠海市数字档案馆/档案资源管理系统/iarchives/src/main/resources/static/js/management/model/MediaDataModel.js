/**
 * Created by Leo on 2019/11/19.
 */
Ext.define('Management.model.MediaDataModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'name', type: 'string',mapping:'title'},
        // {name: 'publishstate', type: 'string'},
        // {name: 'content', type: 'string'},
        // {name: 'filepath', type: 'string'},
        // {name: 'filesize', type: 'int',convert:function (value,record) {
        //     if(value>1048576){//大于1MB
        //         return Math.round(parseFloat((value/1024)/1024)*100)/100 +'MB';
        //     }else{
        //         return Math.round(parseFloat(value/1024)*100)/100+'KB';
        //     }
        // }},
        {name: 'url', type: 'string',mapping:'background',convert:function (value,record) {
            return encodeURIComponent(value);
        }}
    ]
});
