/**
 * Created by Administrator on 2019/5/20.
 */
Ext.define('ReturnWare.view.EntityDetailView',{
    extend: 'Ext.window.Window',
    layout:'border',
    xtype:'entityDetailView',
    title: '档案详细界面',
    width:'92%',
    height:'93%',
    modal:true,
    resizable: true,
    maximizable : true,
    closeToolText:'关闭',
    items:[{
        region:'center',
        flex:1,
        itemId:'entityDetailId',
        layout:'fit',
        html:'<iframe id="frame1" width="100%" height="100%" src="/QRcode/main" style="border:0px"></iframe>'
    }]
});
