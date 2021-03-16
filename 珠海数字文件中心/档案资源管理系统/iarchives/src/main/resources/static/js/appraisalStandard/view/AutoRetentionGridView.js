/**
 * Created by Leo on 2019/5/7 0007.
 */
Ext.define('AppraisalStandard.view.AutoRetentionGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'autoRetentionGridView',
    store:'AutoRetentionGridStore',
    hasCheckColumn:false,
    searchstore:[
        {item: 'word', name: '词条'},
        {item: 'retention', name: '保管期限'},
        {item: 'nums', name: '次数'}
        // ,
        // {item: 'modifydate', name: '最后修改日期'}
    ],
    tbar:[{
        itemId:'initTable',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '初始化词库'
    },
        '-', {
                itemId:'setInitProbability',
                xtype: 'button',
                iconCls:'fa fa-pencil-square-o',
                text: '设置初始概率'
            }
    ],
    columns: [
        {text: '词条', dataIndex: 'word', flex: 2, menuDisabled: true},
        {text: '保管期限', dataIndex: 'retention', flex: 1, menuDisabled: true},
        {text: '次数', dataIndex: 'nums', flex: 1, menuDisabled: true},
        {text: '最后修改日期', dataIndex: 'modifydate', flex: 2, menuDisabled: true}
    ]
});
