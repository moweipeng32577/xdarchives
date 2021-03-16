/**
 * Created by Leo on 2019/04/25.
 */
Ext.define('FileChecker.model.FileCheckerModel',{
    extend:'Ext.data.Model',
    // selectRow: false,
    fields: [
        {name: 'id', type: 'string',mapping:'eleid'},
        {name: 'filename', type: 'string'},
        {name: 'filepath', type: 'string'},
        {name: 'md5', type: 'string'},
        {name: 'pages', type: 'string'},
        {name: 'solid', type: 'string',convert:function (value) {
            if(value== null || value ==""){
                return '未固化'
            }
            else{
                return '已固化'
            }
        }},
        {name: 'resultText', type: 'string'},
        {name: 'lastCheckTime', type: 'string'}

    ]
});