/**
 * Created by Rong on 2018/3/23.
 */
Ext.define('ExchangeReception.view.ExchangeReceptionValidate',{
    extend:'Ext.window.Window',
    xtype:'validateWindow',
    width:600,
    height:400,
    modal:true,
    closeToolText:'关闭',
    title:'',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    defaults:{
        frame:true,
        bodyPadding: 5
    },
    items:[{
        title:'数量',
        itemId:'count',
        flex: 1
    },{
        title:'质量',
        itemId:'quality',
        flex: 2
    },{
        title:'规范性',
        itemId:'norm',
        flex: 1
    }],
    buttons:[{
        text:'关闭',
        itemId:'closeBtn'
    }]
})