/**
 * Created by yl on 2019/1/10.
 * 查看文件
 */
Ext.define('LongRetention.view.LongRetentionDetailView',{
    extend:'Ext.window.Window',
    xtype:'longRetentionDetailView',
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
