/**
 * Created by xd on 2017/10/21.
 */
Ext.define('FindAccount.model.FindAccountGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'userid'},
        {
            name: 'status',
            type: 'string',
            convert:function (value,record) {
                if(record.get('status') =='1'){
                    value = "启用";
                }
                if (record.get('status')=='0') {
                    value = "禁用";
                }
                return value;
            }
        },
        {name: 'createtime', convert: function(value, record) {
            return new Date(value).format("yyyy-MM-dd hh:mm:ss");
        }},
        {name: 'exdate', convert: function(value, record) {
        	if (value != null) {
        		return new Date(value).format("yyyy-MM-dd hh:mm:ss");
        	}
        }},{
            name: 'organ',
            type: 'string',
            convert:function (value,record) {
                value = record.get('organ')['organname'];//取到organ下的organname
                return value;
            }
        }
    ]
});