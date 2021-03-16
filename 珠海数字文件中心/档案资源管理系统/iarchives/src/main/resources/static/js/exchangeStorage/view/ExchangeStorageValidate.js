/**
 * Created by Rong on 2018/3/23.
 */
Ext.define('ExchangeStorage.view.ExchangeStorageValidate',{
    extend:'Ext.window.Window',
    xtype:'validateWindow',
    width:600,
    height:400,
    modal:true,
    closeToolText:'关闭',
    title:'四性验证',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    defaults:{
        frame:true,
        bodyPadding: 5
    },
    items:[{
        title:'准确性',
        itemId:'authenticity',
        flex: 1
    },{
        title:'完整性',
        itemId:'integrity',
        flex: 1
    },{
        title:'可用性',
        itemId:'usability',
        flex: 1
    },{
        title:'安全性',
        itemId:'safety',
        flex: 1
    }],
    buttons:[{
        text:'关闭',
        itemId:'closeBtn'
    }]
})