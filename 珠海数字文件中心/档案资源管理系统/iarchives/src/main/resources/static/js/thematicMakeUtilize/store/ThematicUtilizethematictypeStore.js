/**
 * Created by Administrator on 2019/5/22.
 */
Ext.define('ThematicUtilize.store.ThematicUtilizethematictypeStore',{
    extend:'Ext.data.TreeStore',
    model:'ThematicUtilize.model.ThematicUtilizeTreeModel',
    proxy: {
        type:'ajax',
        url:'/systemconfig/getByConfigcode',
        extraParams:{configcode:'方志馆'},
        reader: {
            type:'json',
            expanded:true
        }
    },
    root:{
        text:'专题类型',
        expanded:true,
        fnid:''
    }
});
