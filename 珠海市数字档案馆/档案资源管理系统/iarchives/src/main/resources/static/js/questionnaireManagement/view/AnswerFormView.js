Ext.define('QuestionnaireManagement.view.AnswerFormView',{
    extend:'Ext.window.Window',
    xtype:'answerFormView',
    itemId:'answerFormViewId',
    // title: '填写问卷',
    frame:true,
    resizable:true,
    width:'60%',
    minWidth:870,
    height:'85%',
    scrollable:true,
    modal:true,
    bodyPadding:60,
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
            bodyPadding:10,
            items:[
                {columnWidth:1,fieldLabel:'',itemId:'questionnaireID',name:'questionnaireID',hidden:true},
                {columnWidth:1,
                    xtype:'tbtext',
                    name:'title',
                    text:'',
                    itemId:'title',
                    hight:'100%',
                    width:'100%',
                    margin:'40 20 10 2',
                    style: {
                        'text-align':'center',
                        'font-size':'22px !important;'
                    }
                }
            ]
        }
    ],
    buttons:[
        { text: '提交',itemId:'submit'}
    ]

});