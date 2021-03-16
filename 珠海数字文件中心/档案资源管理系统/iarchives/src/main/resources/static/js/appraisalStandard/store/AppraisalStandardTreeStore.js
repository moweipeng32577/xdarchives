/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.store.AppraisalStandardTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'AppraisalStandard.model.AppraisalStandardTreeModel',
    proxy: {
        type: 'ajax',
        url: '/appraisalStandard/getAppraisalStandardTree',
        reader: {
            type: 'json'
        }
    },
    root: {
        text: '鉴定类型',
        expanded: true
    }
});