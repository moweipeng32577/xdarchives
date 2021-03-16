/**
 * Created by xd on 2017/10/21.
 */
Ext.define('WhthinManage.store.DzJyTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'WhthinManage.model.DzJyTreeModel',
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
