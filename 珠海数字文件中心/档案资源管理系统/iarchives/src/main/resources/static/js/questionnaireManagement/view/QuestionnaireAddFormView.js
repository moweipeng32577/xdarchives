
var genderStore = Ext.create('Ext.data.Store',{
    fields:['Name','Value'],
    data:[
        {Name:'是',Value:'1'},
        {Name:'否',Value:'0'}
    ]
});

var stickStore = Ext.create('Ext.data.Store',{
    fields:['Name','Value'],
    data:[
        {Name:'1',Value:'1'},
        {Name:'2',Value:'2'},
        {Name:'3',Value:'3'}
    ]
});

var typeStore = Ext.create('Ext.data.Store',{
    fields:['Name','Value'],
    data:[
        {Name:'填空题',Value:'1'},
        {Name:'单选题',Value:'2'},
        {Name:'多选题',Value:'3'}
    ]
});

var isNecessaryStore = Ext.create('Ext.data.Store',{
    fields:['Name','Value'],
    data:[
        {Name:'是',Value:'1'},
        {Name:'否',Value:'0'}
    ]
});

Ext.define('QuestionnaireManagement.view.QuestionnaireAddFormView',{
    extend:'Ext.window.Window',
    xtype:'questionnaireAddFormView',
    itemId:'questionnaireAddFormViewId',
    frame:true,
    resizable:true,
    width:'80%',
    minWidth:610,
    height:'90%',
    scrollable:true,
    // bodyPadding:10,
    // modelValidation:true,
    // layout:'column',
    modal:true,
    closeToolText:'关闭',
    requires:['Ext.layout.container.Border'],
    layout:{
        type:'vbox',
        align:'stretch'
    },
    defaults:{
        layout:'form',
        xtype:'container',
        defaultType:'textfield',
        style:'width:50%'
    },
    items:[
        {
            xtype:'form',
            modelValidation:true,
            layout:'column',
            itemId:'questionnaireAddForm',
            // scrollable:true,//可滚动
            bodyPadding:10,
            items:[
                {columnWidth:1,fieldLabel:'',name:'questionnaireID',hidden:true},
                {columnWidth:1,fieldLabel:'',name:'isanswer',hidden:true},
                {
                    columnWidth: 1,
                    fieldLabel: '标题',
                    name: 'title',
                    margin: '10 0 0 0',
                    allowBlank: false,
                    afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                },
                {
                    columnWidth: 0.5,
                    fieldLabel: '创建时间',
                    name: 'createtime',
                    xtype: 'datefield',
                    value: new Date(),
                    format: 'Y-m-d',
                    allowBlank: false,
                    margin: '10 0 0 0',
                    minValue:new Date(),
                    afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                },
                {
                    columnWidth: 0.5,
                    xtype: 'combobox',
                    name: 'publishstate',
                    fieldLabel: '是否发布',
                    editable: false,
                    store:genderStore,
                    displayField:'Name',
                    valueField:'Value',
                    margin: '10 0 0 20',
                    queryMode:'local'//加载本地数据
                },
                {
                    columnWidth: 0.5,
                    fieldLabel: '开始时间',
                    name: 'starttime',
                    xtype: 'datefield',
                    // value: new Date(),
                    format: 'Y-m-d',
                    margin: '10 0 0 0'
                },
                {
                    columnWidth: 0.5,
                    fieldLabel: '结束时间',
                    name: 'endtime',
                    xtype: 'datefield',
                    // value: new Date(),
                    format: 'Y-m-d',
                    margin: '10 0 0 20',
                    minValue:new Date()
                },
                {
                    columnWidth: 1,
                    xtype:'combobox',
                    fieldLabel:'置顶等级',
                    margin: '10 0 0 0',
                    name:'stick',
                    store:stickStore,
                    editable: false,
                    displayField:'Name',
                    valueField:'Value',
                    queryMode:'local'
                }]
        }
    ],
    buttons:[
        { text: '删除问题',itemId:'questionDel'},
        { text: '添加问题',itemId:'questionAdd'},
        { text: '提交',itemId:'questionnaireAddSubmit'},
        { text: '关闭',itemId:'questionnaireAddClose'}
    ]
});