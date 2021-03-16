/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.view.ClassifySearchFormDirectoryView',{
    extend: 'Ext.form.Panel',
    xtype: 'classifySearchFormDirectoryView',
    itemId:'classifySearchFormDirectoryViewId',
    autoScroll: true,
    title:'当前位置：分类检索',
    fieldDefaults: {
        labelWidth: 100
    },
    layout:'column',
    bodyPadding: 15,
    items:[{//顶部逻辑选择下拉框
        columnWidth:.30,
        fieldLabel:'检索条件关系',
        xtype : 'combo',
        itemId:'topLogicCombo',
        store : [['and','并且'],['or','或者']] ,
        value: 'and',
        name:'logic',
        editable:false//只能从下拉菜单中选择，不可手动编辑
    },{
        columnWidth:.79,
        xtype:'displayfield'
    },{
        columnWidth:.2,
        xtype:'displayfield'
    },{
        xtype: "radiogroup",
        columnWidth: .32,
        itemId: 'datasoureId',
        name: 'datasoure',
        fieldLabel: "数据源",
        labelWidth: 50,
        margin: '0 0 0 5',
        items: [
            {boxLabel: systemLoginType=='0'? '目录中心':'共享平台', inputValue: 'directory',itemId:'directoryId'},
            {boxLabel: '档案系统', inputValue: 'management',itemId:'managementId'}
        ]
    },{//顶部查询按钮
        columnWidth: .1,
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
        hidden : true,
        text:'关闭'
    },{
        columnWidth: .3,
        xtype: 'label',
        text: '温馨提示：红色外框表示输入非法数据！',
        style:{
            color:'red',
            'font-size':'16px'
        },
        margin:'15 0 15 0'
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth: 1,
        xtype:'advancedSearchDynamicForm'
    },{
        columnWidth:.70,
        xtype:'displayfield'
    },{//底部逻辑选择下拉框
        columnWidth:.30,
        fieldLabel:'检索条件关系',
        xtype : 'combo',
        itemId:'bottomLogicCombo',
        store : [['and','并且'],['or','或者']],
        value: 'and',
        margin:'5 0 0 0',
        editable:false//只能从下拉菜单中选择，不可手动编辑
    },{
        columnWidth:.50,
        xtype:'displayfield'
    },{
        columnWidth: .2,
        xtype : 'displayfield'
    },{//底部查询按钮
        columnWidth: .1,
        style:{
            'text-align':'right'
        },
        margin:'30 5 0 0',
        itemId:'bottomSearchBtn',
        xtype : 'button',
        text:'查询'
    },{//底部清除按钮
        columnWidth: .1,
        style:{
            'text-align':'center'
        },
        margin:'30 5 0 0',
        itemId:'bottomClearBtn',
        xtype : 'button',
        text:'清除'
    },{//底部关闭按钮
        columnWidth: .1,
        style:{
            'text-align':'left'
        },
        margin:'30 5 0 0',
        itemId:'bottomCloseBtn',
        xtype : 'button',
        hidden : true,
        text:'关闭'
    },{
        columnWidth:.05,
        xtype:'displayfield'
    }]
});