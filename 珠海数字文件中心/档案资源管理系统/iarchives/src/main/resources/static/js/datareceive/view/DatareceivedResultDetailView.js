/**
 * Created by yl on 2020/6/29.
 */
Ext.define('Datareceive.view.DatareceivedResultDetailView',{
    extend:'Ext.window.Window',
    xtype:'datareceivedResultDetailView',
    width:'50%',
    height:'70%',
    modal:true,
    closeToolText:'关闭',
    title:'详细信息',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    defaults:{
        frame:true,
        autoScroll:true,
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
