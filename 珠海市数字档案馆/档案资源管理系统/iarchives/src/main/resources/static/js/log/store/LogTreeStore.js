/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.store.LogTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Log.model.LogTreeModel',
    proxy: {
        type: 'ajax',
        url: '/log/getLog',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '日志管理',
        expanded: true
    }
});
