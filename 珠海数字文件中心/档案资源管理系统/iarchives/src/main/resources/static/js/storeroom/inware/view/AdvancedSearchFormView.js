/**
 * Created by Administrator on 2019/5/24.
 */
Ext.define('Inware.view.AdvancedSearchFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'advancedSearchFormView',
    itemId:'advancedSearchFormViewId',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 100
    },
    layout:'column',
    bodyPadding: 15,
    items:[{//顶部逻辑选择下拉框
        columnWidth:.16,
        xtype : 'combo',
        fieldLabel:'检索条件关系',
        itemId:'topLogicCombo',
        store : [['and','并且'],['or','或者']] ,
        value: 'and',
        name:'logic',
        editable:false//只能从下拉菜单中选择，不可手动编辑
    },{
        columnWidth:.84,
        xtype:'displayfield'
    },{
        columnWidth: .2,
        xtype : 'displayfield'
    },{//顶部查询按钮
        columnWidth: .13,
        style:{
            'text-align':'right'
        },
        margin:'0 5 0 0',
        itemId:'topSearchBtn',
        xtype : 'button',
        text:'查询'
    },{//顶部清除按钮
        columnWidth: .1,
        style:{
            'text-align':'center'
        },
        margin:'0 5 0 0',
        itemId:'topClearBtn',
        xtype : 'button',
        text:'清除'
    },{//顶部关闭按钮
        columnWidth: .1,
        style:{
            'text-align':'left'
        },
        margin:'0 5 0 0',
        itemId:'topCloseBtn',
        xtype : 'button',
        text:'关闭'
    },{
        columnWidth: .3,
        xtype: 'label',
        text: '温馨提示：红色外框表示输入非法数据！',
        style:{
            color:'red',
            'font-size':'16px'
        },
        margin:'10 0 0 0'
    },{
        columnWidth: .17,
        xtype : 'displayfield'
    },{
        columnWidth: 1,
        xtype:'advancedSearchDynamicForm'
    },{
        columnWidth:.84,
        xtype:'displayfield'
    },{//底部逻辑选择下拉框
        columnWidth:.16,
        fieldLabel:'检索条件关系',
        xtype : 'combo',
        itemId:'bottomLogicCombo',
        store : [['and','并且'],['or','或者']],
        value: 'and',
        margin:'10 0 0 0',
        editable:false//只能从下拉菜单中选择，不可手动编辑
    },{
        columnWidth:.4,
        xtype:'displayfield'
    },{//底部查询按钮
        columnWidth: .13,
        style:{
            'text-align':'right'
        },
        margin:'20 5 0 0',
        itemId:'bottomSearchBtn',
        xtype : 'button',
        text:'查询'
    },{//底部清除按钮
        columnWidth: .1,
        style:{
            'text-align':'center'
        },
        margin:'20 5 0 0',
        itemId:'bottomClearBtn',
        xtype : 'button',
        text:'清除'
    },{//底部关闭按钮
        columnWidth: .1,
        style:{
            'text-align':'left'
        },
        margin:'20 5 0 0',
        itemId:'bottomCloseBtn',
        xtype : 'button',
        text:'关闭'
    },{
        columnWidth:.27,
        xtype:'displayfield'
    }]
});