/**
 * Created by Administrator on 2019/5/28.
 */


Ext.define('WhthinManage.view.PrintEleView', {
    extend: 'Ext.window.Window',
    xtype: 'printEleView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    closeAction: 'hide',
    closable:false,
    layout: 'fit',
    items: [{xtype: 'printEleDetailView'}],
    buttons: [
        {
            xtype:'label',
            itemId:'totalText',
            text:'',
            style:{color:'red'},
            margin:'6 2 5 4'
        },{
            xtype:'label',
            itemId:'nowText',
            text:'',
            style:{color:'red'},
            margin:'6 2 5 6'
        },{
            xtype:'button',
            itemId:'preBtn',
            text:'上一条',
            margin:'-2 2 5 0'
        },{
            xtype:'button',
            itemId:'nextBtn',
            text:'下一条',
            margin:'-2 2 5 2'
        },{
            xtype:'button',
            itemId:'back',
            text:'返回',
            margin:'-2 2 5 2'
        }
    ]
});
