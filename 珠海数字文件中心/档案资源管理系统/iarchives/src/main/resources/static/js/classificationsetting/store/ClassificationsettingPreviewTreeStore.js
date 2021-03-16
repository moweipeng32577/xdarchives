/**
 * Created by tanly on 2018/05/30 0024.
 */
Ext.define('Classificationsetting.store.ClassificationsettingPreviewTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Classificationsetting.model.ClassificationsettingTreeModel',
    proxy: {
        type: 'ajax',
        url: '',
        extraParams:{pcid:''},
        timeout:XD.timeout,
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据节点设置',
        expanded: true,
        fnid:''
    }
});