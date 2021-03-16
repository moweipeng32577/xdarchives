/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('ReturnWare.model.User',{
    extend:'Ext.data.Model',
    fields: [
        { name: 'name', type: 'string' },
        { name: 'age', type: 'int' },
        { name: 'phone', type: 'string' }
    ]
});