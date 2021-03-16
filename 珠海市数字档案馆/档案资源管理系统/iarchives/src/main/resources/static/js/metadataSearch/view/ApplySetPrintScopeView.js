/**
 * Created by Administrator on 2019/5/17.
 */


Ext.define('MetadataSearch.view.ApplySetPrintScopeView', {
    extend: 'Ext.form.Panel',
    xtype: 'applySetPrintScopeView',
    itemId:'applySetPrintScopeViewId',
    title: '设置电子文件打印范围',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 70
    },
    layout: 'column',
    bodyPadding: 16,
    items: [
        {
            xtype: "radiogroup", fieldLabel: "打印范围",
            columnWidth: 1,
            items: [
                {boxLabel: '全部', name: 'printstate', inputValue: '全部', checked: 'true'},
                {xtype: 'displayfield', width: 20},
                {boxLabel: '部分', name: 'printstate', inputValue: '部分'}
            ],
            listeners:{
                'change':function(view,record){
                   if(record.printstate=='全部'){
                       view.findParentByType('applySetPrintScopeView').down('[name=scopepage]').setDisabled(true);
                   }else{
                       view.findParentByType('applySetPrintScopeView').down('[name=scopepage]').setDisabled(false);
                   }
                }
            }
        }, {
            columnWidth: 1,
            xtype: 'textfield',
            name: 'scopepage',
            fieldLabel: "页数范围",
            emptyText:'例如：1-5、8、11-13',
            disabled:true
        }, {
            columnWidth: 1,
            xtype: 'numberfield',
            name: 'copies',
            margin: '10 0 0 0',
            fieldLabel: "打印份数",
            allowBlank: false
        }
    ],

    buttons: [
        { text: '提交',itemId:'stPrintSubmit'},
        { text: '关闭',itemId:'stPrintClose'}
    ]
});
