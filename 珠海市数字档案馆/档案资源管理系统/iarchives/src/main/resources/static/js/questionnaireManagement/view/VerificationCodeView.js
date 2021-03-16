Ext.define('QuestionnaireManagement.view.VerificationCodeView',{
    extend: 'Ext.window.Window',
    xtype:'verificationCodeView',
    title: '提交答卷',
    frame: true,
    // resizable: true,
    closeToolText: '关闭',
    width: 360,
    height:180,
    modal: true,
    layout: 'column',
    items: [{
        xtype: 'form',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        bodyPadding: 15,
        itemId: 'formId',
        layout: 'column',
        width: '100%',
        items: [
            {  columnWidth:0.5,
               xtype: 'image',
               src:'/verificationCode/getVerificationCode?_='+(new Date()).getTime(),
               itemId:'verificationCodeImg',
               width:'40%',
               margin:'10 0 0 10',
               style:{
                    'cursor':'pointer'
                },
               listeners:{
                   el:{//因为Extjs6.2本身并没有提供图片的监听事件，但是Ext的每个组件都可以获得它的dom元素
                       click:function(e,t){
                           var component =this.component;
                           //点击验证码时进行刷新
                           component.setSrc ('/verificationCode/getVerificationCode?_='+(new Date()).getTime());
                       }
                   }
               }
            },
            {   columnWidth:0.4,
                xtype:'textfield',
                allowBlank: false,
                emptyText:'请输入验证码',
                itemId:'verificationCode',
                name:'verificationCode',
                width:'50%',
                height:35,
                margin:'10'
            },{
                columnWidth:1,
                xtype:'displayfield',
                value:'看不清？点击验证码进行刷新',
                magrin:'0 0 0 15',
                padding:'0 0 0 10',
                style:{
                    'font-size': '12px'
                }
            }
        ]
    }],
    buttons: [
        {text: '确定', itemId: 'verificationCodeSubmit'},
        {text: '关闭', itemId: 'verificationCodeClose'}
    ]
});
