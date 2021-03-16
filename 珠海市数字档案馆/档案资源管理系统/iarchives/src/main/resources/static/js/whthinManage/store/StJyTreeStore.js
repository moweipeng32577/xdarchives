/**
 * Created by xd on 2017/10/21.
 */
Ext.define('WhthinManage.store.StJyTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'WhthinManage.model.StJyTreeModel',
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
