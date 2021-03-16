/**
 * Created by Administrator on 2020/6/13.
 */
Ext.define('QuestionnaireManagement.view.QuestionnaireLookFormView',{
    extend:'Ext.window.Window',
    xtype:'questionnaireLookFormView',
    itemId:'questionnaireLookFormViewID',
    frame:true,
    resizable:true,
    width:'60%',
    minWidth:870,
    height:'85%',
    scrollable:true,
    modal:true,
    bodyPadding:60,
    closeToolText:'关闭',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    defaults:{
        layout:'form',
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
                    xtype:'label',
                    itemId:'title',
                    hight:'100%',
                    width:'100%',
                    style: {
                        'text-align':'center',
                        'font-size':'22px !important;'
                    }
                },
                {
                    columnWidth: 1,
                    xtype: 'label',
                    itemId:'ancount',
                    text: '调研份数',
                    margin: '10 0 0 0',
                    style: {
                        'text-align':'right'
                    }
                }
            ]
        }
    ],
    buttons:[
        { text: '关闭',itemId:'closeLook',handler:function (btn) {
            btn.up('questionnaireLookFormView').close();
        }}
    ]

});
