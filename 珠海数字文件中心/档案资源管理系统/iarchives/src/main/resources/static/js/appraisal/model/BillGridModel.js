/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Appraisal.model.BillGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'billid'},
        {name: 'title', type: 'string'},
        {name: 'approvaldate', type: 'string'},
        {name: 'total', type: 'string'},
        {name: 'reason', type: 'string'},
        {name: 'submitter', type: 'string'},
        {name: 'state',convert: function(value) {
            if(value=='0'){return '未送审';}
            if(value=='1'){return '待审核';}
            if(value=='2'){return '已审核';}
            if(value=='3'){return '已审核(不通过)';}
            if(value=='4'){return '已执行';}
            if(value=='5'){return '已退回';}
        }}
    ]
});