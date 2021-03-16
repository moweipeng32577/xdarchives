/**
 * Created by RonJiang on 2018/2/27
 */
Ext.define('Report.store.ReportSxTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Report.model.ReportTreeModel',
    proxy: {
        type: 'ajax',
        url: '/report/getReportDatanode',
        extraParams:{pcid:'',xtType:'声像系统'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '报表管理',
        expanded: true
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
                this.proxy.extraParams.xtType = '声像系统';
            }
        }
    }
});