/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('ReturnWare.store.User',{
    extend:'Ext.data.Store',
    model:'ReturnWare.model.User',
    data: [
        { name: "Tom", age: 5, phone: "123456" },
        { name: "Jerry", age: 3, phone: "654321" }
    ]
});