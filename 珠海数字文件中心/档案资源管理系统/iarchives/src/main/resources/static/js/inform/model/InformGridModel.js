/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Inform.model.InformGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'postedman', type: 'string'},
        {name: 'posteduser', type: 'string'},
        {name: 'postedusergroup', type: 'string'},
        {name: 'stick', type: 'string'},
        {
            name: 'limitdate',
            convert: function (value) {
                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
            }
        },
        {
            name: 'informdate',
            convert: function (value) {
                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
            }
        }
    ]
});