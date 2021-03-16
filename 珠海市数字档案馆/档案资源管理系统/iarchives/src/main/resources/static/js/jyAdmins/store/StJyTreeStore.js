/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.store.StJyTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'JyAdmins.model.StJyTreeModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getJyState',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '审批状态',
        expanded: true
    }
});
