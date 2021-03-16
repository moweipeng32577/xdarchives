/**
 * Created by tanly on 2018/2/1 0001.
 */
Ext.define('Summarization.view.SummarizationSettingView', {
    extend: 'Ext.window.Window',
    xtype: 'SummarizationSettingView',
    itemId: 'SummarizationSettingViewId',
    title: '汇总设置',
    resizable: false,
    width: 685,
    height: 550,
    bodyPadding: '20',
    layout: 'fit',
    modal: true,
    closeToolText: '关闭',
    items: [{
        layout: 'border',
        items: [{
            region: 'west',
            xtype: 'panel',
            title: '汇总对象',
            width: 250,
            border: 1,
            items: [{
                xtype: 'multiselect',
                itemId: 'multiItemId',
                width: 250,
                height: 450,
                allowBlank: true,
                valueField: 'fieldname',
                displayField: 'fieldname',
                store: 'SummarizationSelectStore',
                border:1
            }]
        }, {
            width: 150,
            height: 500,
            xtype: 'panel',
            items: [{
                // region: 'south',
                columnWidth: 1,
                xtype: 'combo',
                itemId:'comboItem',
                width: '81%',
                store: [
                    ['count', '总记录数'],
                    ['max', '最大值'],
                    ['min', '最小值'],
                    ['ave', '平均值'],
                    ['sum', '求和']
                ],
                value: '',
                margin: '170 0 0 10',
                editable: false,
                emptyText:'请选择'
            },{
                xtype: 'button',
                text: '统计',
                itemId:'submitBtn',
                margin: '10 0 0 40'
            }]
        }, {
            region: 'east',
            xtype: 'panel',
            title: '汇总结果',
            width: 250,
            border: 1,
            layout: 'border',
            items: [{
                region: 'center',
                xtype: 'textarea',
                itemId:'textareaItem',
                border: false,
                width: 250,
                height: 450,
                readOnly: true
            }, {
                region: 'south',
                xtype: 'button',
                itemId:'resetBtn',
                text: '清空'
            }]
        }]
    }]
});
