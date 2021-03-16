/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.view.AppraisalStandardGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'appraisalStandardGridView',
    store:'AppraisalStandardGridStore',
    searchstore:[
        {item: 'appraisalstandardvalue', name: '鉴定标准值'},
        {item: 'appraisalretention', name: '保管期限'},
        {item: 'appraisaldesc', name: '描述'}
    ],
    tbar:[{
        itemId:'save',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
    }, '-', {
        itemId:'modify',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        itemId:'del',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    }],
    columns: [
        {text: '鉴定类型', dataIndex: 'appraisaltypevalue', flex: 3, menuDisabled: true},
        {text: '鉴定标准值', dataIndex: 'appraisalstandardvalue', flex: 2, menuDisabled: true},
        {text: '保管期限', dataIndex: 'appraisalretention', flex: 1, menuDisabled: true},
        {text: '描述', dataIndex: 'appraisaldesc', flex: 2, menuDisabled: true}
    ]
});